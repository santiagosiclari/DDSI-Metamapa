package Metamapa.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceIncidencias {

private final RestTemplate restTemplate;
private final String baseUrl;

public ServiceIncidencias(RestTemplate restTemplate,
                            @Value("${incidencias.service.url}") String baseUrl) {
  this.restTemplate = restTemplate;
  this.baseUrl = baseUrl;
}
}