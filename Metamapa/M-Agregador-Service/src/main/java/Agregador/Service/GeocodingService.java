package Agregador.Service;
import jakarta.annotation.PostConstruct;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.index.strtree.STRtree;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeocodingService {
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final STRtree spatialIndex = new STRtree();
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        var shpUrl = getClass().getResource("/data/provinciaPolygon.shp");
        if (shpUrl == null) {
            throw new IllegalStateException(
                    "No se encontró provinciaPolygon.shp en resources/data/"
            );
        }
        PreparedGeometryFactory preparedFactory = new PreparedGeometryFactory();
        ShapefileDataStore store = new ShapefileDataStore(shpUrl);
        store.setCharset(StandardCharsets.UTF_8);
        var source = store.getFeatureSource();
        try (var features = source.getFeatures().features()) {
            while (features.hasNext()) {
                var feature = features.next();
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                String provincia = String.valueOf(feature.getAttribute("nam"));
                PreparedGeometry prepared = preparedFactory.create(geom);
                spatialIndex.insert(
                        geom.getEnvelopeInternal(),
                        new Entry(provincia, prepared)
                );
            }
        }
        spatialIndex.build();
    }

    public String obtenerProvincia(double lat, double lon) {
        String key = lat + "," + lon;
        return cache.computeIfAbsent(key, k -> resolverProvincia(lat, lon));
    }

    private String resolverProvincia(double lat, double lon) {
        //System.out.printf("Buscando provincia a partir de coordenadas (%.6f, %.6f)%n",
        //        lat, lon
        //);
        Point point = geometryFactory.createPoint(
                new Coordinate(lon, lat) // x=lon, y=lat
        );
        Envelope env = new Envelope(lon, lon, lat, lat);
        @SuppressWarnings("unchecked")
        List<Entry> candidates = spatialIndex.query(env);
        for (Entry e : candidates) {
            if (e.geom.covers(point)) {
                return e.nombre;
            }
        }
        return "Provincia Desconocida";
    }

    /** Entrada del índice espacial */
    private static class Entry {
        final String nombre;
        final PreparedGeometry geom;
        Entry(String nombre, PreparedGeometry geom) {
            this.nombre = nombre;
            this.geom = geom;
        }
    }
}