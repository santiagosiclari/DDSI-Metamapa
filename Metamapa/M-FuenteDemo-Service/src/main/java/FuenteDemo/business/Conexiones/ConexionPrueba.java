package java.FuenteDemo.business.Conexiones;
import java.time.*;
import java.util.*;

public class ConexionPrueba extends Conexion {
    private int contador = 0;

    @Override
    public Map<String, Object> siguienteHecho(String url, LocalDateTime fechaUltimaConsulta) {
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