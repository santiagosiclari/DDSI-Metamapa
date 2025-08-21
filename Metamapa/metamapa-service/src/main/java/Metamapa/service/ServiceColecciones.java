package Metamapa.service;

import Metamapa.business.Colecciones.Coleccion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ServiceColecciones {
  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceColecciones(RestTemplate restTemplate,
                            @Value("${colecciones.service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public ArrayList<Coleccion> getColecciones() {
    String url = String.format("%s/api-colecciones/", baseUrl);
    return restTemplate.getForObject(url, ArrayList.class);
  }

  public Coleccion getColeccion(UUID uuid) {
    String url = String.format("%s/api-colecciones/%s", baseUrl, uuid);
    return restTemplate.getForObject(url, Coleccion.class);
  }

  public UUID crearColeccion(String titulo, String descripcion, String consenso,
                             List<Map<String, Object>> pertenencia,
                             List<Map<String, Object>> noPertenencia) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("titulo", titulo);
    payload.put("descripcion", descripcion);
    payload.put("consenso", consenso);
    payload.put("criteriosPertenencia", pertenencia);
    payload.put("criteriosNoPertenencia", noPertenencia);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

    @SuppressWarnings("unchecked")
    Map<String, Object> response = restTemplate.postForObject(
            baseUrl + "/api-colecciones/",
            request,
            Map.class
    );
    return (UUID) response.get("handle");
  }

  public void deleteColeccion(UUID uuid) {
    String url = String.format("%s/api-colecciones/%s", baseUrl, uuid);
    try {
      restTemplate.delete(url);
    } catch (Exception e) {
      System.err.println("Error al eliminar la colección: " + e.getMessage());
    }
  }

  public boolean actualizarAlgoritmoConsenso(UUID idColeccion, String algoritmo) {
    String url = String.format("%s/api-colecciones/%s/consenso/%s", baseUrl, idColeccion, algoritmo);
    ResponseEntity<Void> resp = restTemplate.exchange(url, HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);
    return resp.getStatusCode().is2xxSuccessful();
  }
}