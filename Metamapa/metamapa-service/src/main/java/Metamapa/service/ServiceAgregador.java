package Metamapa.service;
import Metamapa.business.Agregador.Agregador;
import Metamapa.business.Hechos.Hecho;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
  //TODO: CHEQUEAR
  public enum Result { OK, NOT_FOUND, CONFLICT, INVALID }
  public Result aprobarSolicitudEliminacion(UUID id) {
    return resolverRemoto(id, "APROBAR");
  }
  public Result rechazarSolicitudEliminacion(UUID id) {
    return resolverRemoto(id, "RECHAZAR");
  }
  private Result resolverRemoto(UUID id, String accion) {
    String url = String.format("%s/api-agregador/solicitudesEliminacion/%s?accion=%s", baseUrl, id, accion);
    try {
      var resp = restTemplate.exchange(url, HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);
      return resp.getStatusCode().is2xxSuccessful() ? Result.OK : Result.INVALID;
    } catch (HttpClientErrorException.NotFound e) { return Result.NOT_FOUND;
    } catch (HttpClientErrorException.Conflict e) { return Result.CONFLICT;
    } catch (HttpClientErrorException.UnprocessableEntity e) { return Result.INVALID; }
  }
}