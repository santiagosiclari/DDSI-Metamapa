package Metamapa.service;
import Metamapa.business.Agregador.Agregador;
import Metamapa.business.Hechos.Hecho;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ServiceAgregador {
  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceAgregador(RestTemplate restTemplate,
                         @Value("${M.Agregador.Service.url}") String baseUrl) {
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
    String url = String.format("%s/api-agregador/fuentesDeDatos/actualizar", baseUrl);
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

  public void agregarFuenteAColeccion(UUID idColeccion, Integer idFuente){
    String url = String.format("%s/api-agregador/fuentesDeDatos/%s/%d", baseUrl, idColeccion, idFuente);
    restTemplate.postForObject(url, null, Void.class);
  }

  //TODO: CHEQUEAR
  public enum Result { OK, NOT_FOUND, CONFLICT, INVALID }
  public Result resolverRemoto(Integer id, String accion) {
    var uri = UriComponentsBuilder
            .fromHttpUrl(baseUrl.replaceAll("/+$",""))
            .path("/api-solicitudes/solicitudesEliminacion/{id}")
            .buildAndExpand(id)
            .toUri();

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    var body = java.util.Map.of("accion", accion); // {"accion":"APROBAR"} o {"accion":"RECHAZAR"}
    var entity = new HttpEntity<>(body, headers);

    try {
      var resp = restTemplate.exchange(uri, HttpMethod.PATCH, entity, Void.class);
      return resp.getStatusCode().is2xxSuccessful() ? Result.OK : Result.INVALID;
    } catch (HttpClientErrorException.NotFound e) { return Result.NOT_FOUND;
    } catch (HttpClientErrorException.Conflict e) { return Result.CONFLICT;
    } catch (HttpClientErrorException.UnprocessableEntity e) { return Result.INVALID; }
  }

  public Result aprobarSolicitudEliminacion(Integer id)  { return resolverRemoto(id, "APROBAR");  }
  public Result rechazarSolicitudEliminacion(Integer id) { return resolverRemoto(id, "RECHAZAR"); }
    public Integer crearSolicitudEliminacionYRetornarId(Integer idHechoAfectado, String motivo, String url) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("idHechoAfectado", idHechoAfectado);
        payload.put("motivo", motivo);
        if (url != null && !url.isBlank()) {
            payload.put("url", url);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(
                baseUrl + "/api-solicitudes/solicitudesEliminacion", // corregido
                request,
                Map.class
        );

        return (Integer) response.get("id");
    }

    public Integer crearSolicitudEdicionYRetornarId(String hechoAfectado, String motivo, String url) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("hechoAfectado", hechoAfectado);
        payload.put("motivo", motivo);
        if (url != null && !url.isBlank()) {
            payload.put("url", url);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(
                baseUrl + "/api-solcitudEdicion/",
                request,
                Map.class
        );
        return (Integer) response.get("id");
    }

  public Map<String,Object> obtenerSolicitudEliminacion(Integer id) {
    String url = baseUrl.replaceAll("/+$","") + "/api-solicitudes/solicitudesEliminacion/" + id;

    try {
      @SuppressWarnings("unchecked")
      Map<String,Object> response = restTemplate.getForObject(url, Map.class);
      return response;
    } catch (HttpClientErrorException.NotFound e) {
      return null; // Not Found
    }
  }

}