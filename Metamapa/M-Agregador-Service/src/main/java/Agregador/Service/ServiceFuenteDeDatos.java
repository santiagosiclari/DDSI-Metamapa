package Agregador.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import Agregador.business.Consenso.Consenso;
import Agregador.persistencia.RepositorioHechos;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Agregador.business.Hechos.*;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ServiceFuenteDeDatos {
  private final RestTemplate restTemplate;
  private final RepositorioHechos repositorioHechos;

  @Value("${M.FuenteDinamica.Service.url}") private String urlDinamica;
  @Value("${M.FuenteEstatica.Service.url}") private String urlEstatica;
  @Value("${M.FuenteProxy.Service.url}")    private String urlProxy;

  private static final int FACTOR_TIPO = 1_000_000;

  // ==== Rutas (cambiá acá si loaders exponen distinto) ====
  private static final String PATH_HECHOS_POR_FUENTE = "%s/fuentesDeDatos/%d/hechos"; // o "%s/%d/hechos"
  private static final String PATH_LISTAR_FUENTES    = "%s/fuentesDeDatos";           // o "%s/"

  public ServiceFuenteDeDatos(RestTemplate rt, RepositorioHechos repo) {
    this.restTemplate = rt;
    this.repositorioHechos = repo;
  }

  // ================== Router ==================
  private String resolverBaseUrl(Integer idFuente) {
    int tipo = idFuente / FACTOR_TIPO; // 1=dinámica, 2=estática, 3=proxy
    return switch (tipo) {
      case 1 -> urlDinamica;
      case 2 -> urlEstatica;
      case 3 -> urlProxy;
      default -> throw new IllegalArgumentException("Tipo de fuente desconocido para id=" + idFuente);
    };
  }

  private String hechosUrl(Integer idFuente) {
    return String.format(PATH_HECHOS_POR_FUENTE, resolverBaseUrl(idFuente), idFuente);
  }

  // ================== API ==================

  /** Trae hechos de UNA fuente (sin filtros) y los mapea a tu dominio. */
  public List<Hecho> getHechosDeFuente(int idFuente) {
    String url = hechosUrl(idFuente);
    ResponseEntity<List<Map<String,Object>>> resp = restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
    );
    List<Map<String,Object>> raw = Optional.ofNullable(resp.getBody()).orElseGet(List::of);
    return raw.stream().map(json -> jsonToHecho(json, idFuente)).toList();
  }

  /** Trae hechos de UNA fuente aplicando filtros (query params). */
  public List<Hecho> getHechosDeFuente(int idFuente, Map<String, ?> filtros) {
    UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(hechosUrl(idFuente));
    if (filtros != null) filtros.forEach((k, v) -> {
      if (v == null) return;
      if (v instanceof Collection<?> c) c.forEach(it -> b.queryParam(k, String.valueOf(it)));
      else b.queryParam(k, String.valueOf(v));
    });
    ResponseEntity<List<Map<String,Object>>> resp = restTemplate.exchange(
            b.build(true).toUri(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
    );
    List<Map<String,Object>> raw = Optional.ofNullable(resp.getBody()).orElseGet(List::of);
    return raw.stream().map(json -> jsonToHecho(json, idFuente)).toList();
  }

  /** Lista todas las fuentes (dinámicas+estáticas+proxy). */
  public List<FuenteInfoDTO> obtenerFuentesDeDatos() {
    List<FuenteInfoDTO> total = new ArrayList<>();
    total.addAll(fetchFuentes(urlDinamica));
    total.addAll(fetchFuentes(urlEstatica));
    total.addAll(fetchFuentes(urlProxy));
    return total;
  }

  private List<FuenteInfoDTO> fetchFuentes(String base) {
    String url = String.format(PATH_LISTAR_FUENTES, base);
    ResponseEntity<List<FuenteInfoDTO>> resp = restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
    );
    return Optional.ofNullable(resp.getBody()).orElseGet(List::of);
  }

  /** Devuelve SOLO los hechos nuevos respecto a tu repositorio (útil para sincronización). */
  public List<Hecho> getHechosNuevosDeFuente(int idFuente) {
    return getHechosDeFuente(idFuente).stream()
            .filter(h -> !existeHecho(h)) // <--- FIX: antes lo tenías al revés
            .toList();
  }

  public boolean existeHecho(Hecho h) {
    return repositorioHechos.getHechos().stream().anyMatch(h2 -> Consenso.sonIguales(h, h2));
  }

  // ================== Mapping ==================

  @SuppressWarnings("unchecked")
  private Hecho jsonToHecho(Map<String,Object> json, int idFuente) {
    String titulo       = str(json.get("titulo"));
    String descripcion  = str(json.get("descripcion"));
    String categoria    = str(json.get("categoria"));
    Float latitud       = f(json.get("latitud"));
    Float longitud      = f(json.get("longitud"));
    LocalDate fechaHecho= date(json.get("fechaHecho"));

    // id del hecho dentro de la fuente (aceptamos "id" o "hechoId")
    Integer hechoId     = i(json.containsKey("id") ? json.get("id") : json.get("hechoId"));

    Boolean anonimo     = bool(json.get("anonimo"));
    // Si tu remoto lo manda:
    Boolean eliminado   = bool(json.get("eliminado"));
    LocalDate fechaCarga= date(json.get("fechaCarga"));
    LocalDate fechaMod  = date(json.get("fechaModificacion"));

    // Construcción del dominio (perfil/multimedia opcionales -> null / vacío)
    Hecho h = new Hecho(
            titulo, descripcion, categoria, latitud, longitud, fechaHecho,
            null,                // perfil
            idFuente,            // fuenteId (clave para CriterioFuenteDeDatos)
            hechoId == null ? 0 : hechoId,
            anonimo != null ? anonimo : Boolean.TRUE,
            new ArrayList<>()    // multimedia
    );
    if (fechaCarga != null) h.setFechaCarga(fechaCarga);
    if (fechaMod != null)   h.setFechaModificacion(fechaMod);
    if (eliminado != null)  h.setEliminado(eliminado);

    // metadata (si viene anidada)
    Map<String,Object> meta = (Map<String,Object>) json.get("metadata");
    if (meta != null) {
      HashMap<String,String> md = new HashMap<>();
      meta.forEach((k,v) -> md.put(k, String.valueOf(v)));
      h.setMetadata(md);
    }
    return h;
  }

  // ================== Helpers de parseo ==================
  private static String str(Object o)        { return o == null ? null : String.valueOf(o); }
  private static Integer i(Object o)         { try { return o == null ? null : Integer.valueOf(String.valueOf(o)); } catch(Exception e){ return null; } }
  private static Float f(Object o)           { try { return o == null ? null : Float.valueOf(String.valueOf(o)); } catch(Exception e){ return null; } }
  private static Boolean bool(Object o)      { return o == null ? null : Boolean.valueOf(String.valueOf(o)); }
  private static LocalDate date(Object o)    { try { return o == null ? null : LocalDate.parse(String.valueOf(o)); } catch(Exception e){ return null; } }

  // ================== DTOs simples ==================
  @Data
  public static class FuenteInfoDTO {
    private Integer id;
    private String nombre;
  }
}