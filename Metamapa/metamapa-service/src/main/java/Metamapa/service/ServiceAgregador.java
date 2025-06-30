package Metamapa.service;


import domain.business.Agregador.Agregador;
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

  public Agregador getAgregador(Integer idAgregador) {
    String url = String.format("%s/agregador/%d/hechos", baseUrl, idAgregador);
    return restTemplate.getForObject(url, Agregador.class);
  }
}