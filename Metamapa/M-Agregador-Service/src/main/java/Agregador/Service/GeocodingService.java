package Agregador.Service;
import jakarta.annotation.PostConstruct;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.index.strtree.STRtree;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class GeocodingService {
    private final GeometryFactory gf = new GeometryFactory();
    private final STRtree index = new STRtree();

    @PostConstruct
    public void init() throws Exception {
        var shpUrl = getClass().getResource("/data/provinciaPolygon.shp");
        System.out.println(shpUrl);
        if (shpUrl == null) {
            throw new IllegalStateException("No se encontró el archivo provinciaPolygon.shp en resources/data/");
        }
        var store = new ShapefileDataStore(shpUrl);
        store.setCharset(StandardCharsets.UTF_8);
        var source = store.getFeatureSource();
        try (var features = source.getFeatures().features()) {
            while (features.hasNext()) {
                var f = features.next();
                Geometry geom = (Geometry) f.getDefaultGeometry();
                String nombreProvincia = String.valueOf(f.getAttribute("nam"));
                index.insert(geom.getEnvelopeInternal(), new Entry(nombreProvincia, geom));
            }
        }
        index.build();
    }

    public String obtenerProvincia(double lat, double lon) {
        Point p = gf.createPoint(new Coordinate(lon, lat)); // x=lon, y=lat
        @SuppressWarnings("unchecked")
        List<Entry> candidates = index.query(p.getEnvelopeInternal());
        for (Entry e : candidates) {
            if (e.geom.covers(p)) {
                return e.nombre;
            }
        }
        return "Provincia Desconocida";
    }

    private static class Entry {
        final String nombre;
        final Geometry geom;
        Entry(String nombre, Geometry geom) { this.nombre = nombre; this.geom = geom; }
    }
}