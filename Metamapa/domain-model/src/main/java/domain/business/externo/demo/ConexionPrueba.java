package domain.business.externo.demo;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ConexionPrueba extends Conexion {
    private int contador = 0;

    @Override
    public Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta) {
        if (contador > 2) return null;
        contador++;

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", "h" + contador);
        datos.put("descripcion", "Hecho generado " + contador);
        datos.put("categoria", "CategoriaDemo");
        datos.put("latitud", 10.0f + contador);
        datos.put("longitud", 20.0f + contador);
        datos.put("fecha", LocalDate.now());
        return datos;
    }
}
