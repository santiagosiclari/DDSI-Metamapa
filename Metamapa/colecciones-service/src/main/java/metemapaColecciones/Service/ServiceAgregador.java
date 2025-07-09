package metemapaColecciones.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Map;

@Service
public class ServiceAgregador {
  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceAgregador(RestTemplate restTemplate, @Value("${agregador.service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }


  public ArrayList<Map<String,Object>> obtenerHechos()
  {
    String url = String.format("%s/hechos", baseUrl);
    return restTemplate.getForObject(url, ArrayList.class);
  }





}
