package domain.business.externo.demo;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

public abstract class Conexion {
    /**
     * Devuelve un mapa con los atributos de un hecho, indexados por nombre de
     * atributo. Si el metodo retorna null, significa que no hay nuevos hechos
     * por ahora. La fecha es opcional
     */
    public abstract Map<String, Object> siguienteHecho(String url, LocalDateTime fechaUltimaConsulta);
/*            if (Math.random() > 0.5) { // Randomiza si devuelve hecho o no
        Map<String, Object> hecho = new HashMap<>();
        hecho.put("id", "demo1");
        hecho.put("descripcion", "Hecho demo");
        hecho.put("categoria", "Test");
        hecho.put("latitud", 10.0f);
        hecho.put("longitud", 20.0f);
        hecho.put("fecha", LocalDate.now());
        return hecho;
    }
        return null;*/
}
