package Agregador.Service;
import Agregador.business.Hechos.Hecho;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import Agregador.business.Consenso.Consenso;
import Agregador.persistencia.RepositorioHechos;
import lombok.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ServiceFuenteDeDatos {
  private final RestTemplate restTemplate;
  private final RepositorioHechos repositorioHechos;
  private final Normalizador normalizador;
  private static final int FACTOR_TIPO = 1_000_000;

  // ==== Rutas (cambiá acá si loaders exponen distinto) ====
  //private static final String PATH_HECHOS_POR_FUENTE = "%s/fuentesDeDatos/%d/hechos"; // o "%s/%d/hechos"
  private static final String PATH_LISTAR_FUENTES    = "%s/fuentesDeDatos";           // o "%s/"

  // ================== API ==================
  /** Trae hechos de UNA fuente (sin filtros) y los mapea a tu dominio. */
  public List<Hecho> getHechosDeFuente(String urlBase) {
    String url = urlBase + "/api-fuentesDeDatos/hechos";
    ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
    );
    List<Map<String, Object>> raw = Optional.ofNullable(resp.getBody()).orElseGet(List::of);
    // imprimir raw para debug
    //System.out.println("Hechos raw de la fuente " + urlBase + ": " + raw);
    return raw.stream()
            .map(json -> {
              Object fuenteIdRaw = json.get("fuenteId");
              if (fuenteIdRaw == null)
                throw new IllegalArgumentException("Falta 'fuenteId' en el hecho: " + json);
              int fuenteId = (Integer) fuenteIdRaw;
              return jsonToHecho(json, fuenteId);
            })
            .toList();
  }

  public void actualizarHechos(String url) {
    List<Hecho> hechos = getHechosDeFuente(url);
    // Normalizar y persistir
    repositorioHechos.saveAll(normalizador.normalizarYUnificar(hechos));
    System.out.println("Total hechos guardados: " + repositorioHechos.findAll().size());
  }

  /** Trae hechos de UNA fuente aplicando filtros (query params). */
  //Arreglar
//  public List<Hecho> getHechosDeFuente(int idFuente, Map<String, ?> filtros) {
//    UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(hechosUrl(idFuente));
//    if (filtros != null) filtros.forEach((k, v) -> {
//      if (v == null) return;
//      if (v instanceof Collection<?> c) c.forEach(it -> b.queryParam(k, String.valueOf(it)));
//      else b.queryParam(k, String.valueOf(v));
//    });
//    ResponseEntity<List<Map<String,Object>>> resp = restTemplate.exchange(
//            b.build(true).toUri(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
//    );
//    List<Map<String,Object>> raw = Optional.ofNullable(resp.getBody()).orElseGet(List::of);
//    return raw.stream().map(json -> jsonToHecho(json, idFuente)).toList();
//  }

  /** Lista todas las fuentes (dinámicas+estáticas+proxy). */
  private List<FuenteInfoDTO> fetchFuentes(String base) {
    String url = String.format(PATH_LISTAR_FUENTES, base);
    ResponseEntity<List<FuenteInfoDTO>> resp = restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
    );
    return Optional.ofNullable(resp.getBody()).orElseGet(List::of);
  }

  /** Devuelve SOLO los hechos nuevos respecto a tu repositorio (útil para sincronización). */
  public List<Hecho> getHechosNuevosDeFuente(String urlBase) {
    return getHechosDeFuente(urlBase).stream()
            .filter(h -> !existeHecho(h)) // <--- FIX: antes lo tenías al revés
            .toList();
  }

  public boolean existeHecho(Hecho h) {
    return repositorioHechos.findAll().stream().anyMatch(h2 -> Consenso.sonIguales(h, h2));
  }

  // ================== Mapping ==================
  @SuppressWarnings("unchecked")
  private Hecho jsonToHecho(Map<String,Object> json, Integer idFuente) {
    String titulo       = str(json.get("titulo"));
    String descripcion  = str(json.get("descripcion"));
    String categoria    = str(json.get("categoria"));
    Float latitud       = f(json.get("latitud"));
    Float longitud      = f(json.get("longitud"));
    LocalDateTime fechaHecho = date(json.get("fechaHecho"));
    // id del hecho dentro de la fuente (aceptamos "id" o "hechoId")
    Integer hechoId     = i(json.containsKey("id") ? json.get("id") : json.get("hechoId"));
    Boolean anonimo     = bool(json.get("anonimo"));
    // Si tu remoto lo manda:
    Boolean eliminado   = bool(json.get("eliminado"));
    LocalDateTime fechaCarga = date(json.get("fechaCarga"));
    LocalDateTime fechaMod  = date(json.get("fechaModificacion"));
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
  private static LocalDateTime date(Object o)    { try { return o == null ? null : LocalDateTime.parse(String.valueOf(o)); } catch(Exception e){ return null; } }

  // ================== DTOs simples ==================
  @Data
  public static class FuenteInfoDTO {
    private Integer id;
    private String nombre;
  }
}