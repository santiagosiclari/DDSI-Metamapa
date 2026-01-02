package Agregador.Service;

import jakarta.annotation.PostConstruct;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.index.strtree.STRtree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeocodingService {
    private static final Logger log = LoggerFactory.getLogger(GeocodingService.class);
    @Value("${rutas.provincias}")
    private String rutaProvincias;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final STRtree spatialIndex = new STRtree();
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private volatile boolean isReady = false;

    @PostConstruct
    public void init() throws Exception {
        Thread loaderThread = new Thread(() -> {
            try {
                cargarShapefile();
            } catch (Exception e) {
                log.error("❌ ERROR CRÍTICO cargando el mapa de provincias: ", e);
            }
        });
        loaderThread.setName("Shapefile-Loader-Thread");
        loaderThread.start();
    }

    private void cargarShapefile() throws Exception {
        log.info("📡 Iniciando carga del Shapefile en segundo plano: {}", rutaProvincias);

        File file = new File(rutaProvincias);
        if (!file.exists()) {
            log.error("🚨 ARCHIVO NO ENCONTRADO: {}", rutaProvincias);
            return;
        }

        URL shpUrl = file.toURI().toURL();
        PreparedGeometryFactory preparedFactory = new PreparedGeometryFactory();

        // Configuramos el DataStore
        ShapefileDataStore store = new ShapefileDataStore(shpUrl);
        store.setCharset(StandardCharsets.UTF_8);

        var source = store.getFeatureSource();
        try (var features = source.getFeatures().features()) {
            int count = 0;
            while (features.hasNext()) {
                var feature = features.next();
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                String provincia = String.valueOf(feature.getAttribute("nam"));

                PreparedGeometry prepared = preparedFactory.create(geom);

                // Sincronizamos la inserción por seguridad
                synchronized (spatialIndex) {
                    spatialIndex.insert(geom.getEnvelopeInternal(), new Entry(provincia, prepared));
                }
                count++;
            }
            synchronized (spatialIndex) {
                spatialIndex.build();
            }
            this.isReady = true; // El mapa ya se puede usar
            log.info("✅ Carga completada. {} provincias listas para geocoding.", count);
        }
    }

    public String obtenerProvincia(double lat, double lon) {
        if (!isReady) {
            return "Cargando Mapa...";
        }
        String key = String.format(Locale.ROOT, "%.4f,%.4f", lat, lon);
        return cache.computeIfAbsent(key, k -> resolverProvincia(lat, lon));
    }

    private String resolverProvincia(double lat, double lon) {
        Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
        Envelope env = new Envelope(lon, lon, lat, lat);

        @SuppressWarnings("unchecked")
        List<Entry> candidates;
        synchronized (spatialIndex) {
            candidates = spatialIndex.query(env);
        }

        for (Entry e : candidates) {
            if (e.geom.covers(point)) {
                return e.nombre;
            }
        }
        return "Provincia Desconocida";
    }

    private static class Entry {
        final String nombre;
        final PreparedGeometry geom;
        Entry(String nombre, PreparedGeometry geom) {
            this.nombre = nombre;
            this.geom = geom;
        }
    }
}