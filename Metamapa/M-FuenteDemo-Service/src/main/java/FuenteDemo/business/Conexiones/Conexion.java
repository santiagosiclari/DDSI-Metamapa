package FuenteDemo.business.Conexiones;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import FuenteDemo.business.Hechos.Hecho;

public class Conexion {
    /**
     * Devuelve un mapa con los atributos de un hecho, indexados por nombre de
     * atributo. Si el metodo retorna null, significa que no hay nuevos hechos
     * por ahora. La fecha es opcional
     */


    public Conexion() {
    }

    public Map<String, Object> siguienteHecho(String UrlBase,LocalDateTime fechaUltimaConsulta)
    {
        RestTemplate rest = new RestTemplate();

        return rest.getForObject(UrlBase + "?fechaDesde=" + fechaUltimaConsulta,Map.class);
    }


}