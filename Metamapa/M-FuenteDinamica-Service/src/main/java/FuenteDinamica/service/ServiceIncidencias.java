/*package FuenteDinamica.service;


import domain.business.incidencias.Hecho;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceIncidencias {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceIncidencias(RestTemplate restTemplate, @Value("${incidencias.service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }


  public ArrayList<Hecho> obtenerHechosXIDFuente(Integer idFuenteDeDatos)
  {
    String url = String.format("%s/api-incidencias/hechos/%d", baseUrl,idFuenteDeDatos);
    return restTemplate.getForObject(url, ArrayList.class);
  }
}*/