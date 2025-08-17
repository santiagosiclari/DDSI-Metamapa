package Agregador.Service;


import Agregador.business.deprecado.FuentesDeDatos.FuenteDeDatos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.ArrayList;
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

  
  //TODO no es necesario esto ya que el agregador cuenta con la fuente y la misma tiene los hechos.
  public ArrayList<Map<String,Object>> getHechosDeFuente(int idFuente)
  {
    String url = String.format("%s/%d/hechos", baseUrl, idFuente);
    return restTemplate.getForObject(url,ArrayList.class);
  }
  public ArrayList<FuenteDeDatos> obtenerFuenteDeDatos() {
    String url = String.format("%s/api-fuentesDeDatos/", baseUrl);
    return restTemplate.getForObject(url, ArrayList.class);
  }
}