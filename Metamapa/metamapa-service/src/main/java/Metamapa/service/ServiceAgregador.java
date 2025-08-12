package Metamapa.service;
import Metamapa.business.Agregador.Agregador;
import Metamapa.business.incidencias.Hecho;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceAgregador {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceAgregador(RestTemplate restTemplate,
                         @Value("${agregador.service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }
//TODO esta no se va a usar(solo la deberia usar las colecciones), solo para pruebas
  public ArrayList<Hecho> getAgregadorHechos() {
    String url = String.format("%s/api-agregador/hechos", baseUrl);
    return restTemplate.getForObject(url, ArrayList.class);
  }

  public Agregador getAgregador() {
    String url = String.format("%s/api-agregador/", baseUrl);
    return restTemplate.getForObject(url, Agregador.class);
  }

  public void actualizarAgregador() {
    String url = String.format("%s/api-agregador/fuentes/actualizar", baseUrl);
    restTemplate.postForObject(url, null, Void.class);
  }
  public void agregarFuente(Integer idFuente){
    String url = String.format("%s/api-agregador/fuentesDeDatos/agregar/%d", baseUrl, idFuente);
    restTemplate.postForObject(url, null, Void.class);
  }
  public void removerFuente(Integer idFuente){
    String url = String.format("%s/api-agregador/fuentesDeDatos/remover/%d", baseUrl, idFuente);
    restTemplate.postForObject(url, null, Void.class);
  }
}