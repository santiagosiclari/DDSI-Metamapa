package metemapaAgregador.Service;

import domain.business.FuentesDeDatos.FuenteDeDatos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceFuenteDeDatos {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceFuenteDeDatos(RestTemplate restTemplate,
                              @Value("${fuentes.service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public FuenteDeDatos getFuenteDeDatos(Integer idFuente) {
    String url = String.format("%s/api-fuentesDeDatos/%d", baseUrl, idFuente);
    return restTemplate.getForObject(url, FuenteDeDatos.class);
  }
}