package Metamapa.service;

import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.Hechos.Multimedia;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceFuenteDeDatos {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  // Bases de cada micro (dejarlas tal cual en application.properties)
  private final String urlDinamica; // ej: http://localhost:9001/api-fuentesDeDatos
  private final String urlEstatica; // ej: http://localhost:9002/api-fuentesDeDatos
  private final String urlProxy;    // ej: http://localhost:9003/api-fuentesDeDatos

  public ServiceFuenteDeDatos(RestTemplate restTemplate,
                              @Value("${M.FuenteDinamica.Service.url}") String urlDinamica,
                              @Value("${M.FuenteEstatica.Service.url}") String urlEstatica,
                              @Value("${M.FuenteProxy.Service.url}") String urlProxy,
                              ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    // Normalizo quitando slash final; luego uso join(...) para agregarlo cuando haga falta
    this.urlDinamica = trimTrailingSlash(urlDinamica);
    this.urlEstatica = trimTrailingSlash(urlEstatica);
    this.urlProxy    = trimTrailingSlash(urlProxy);
  }

  // Mapa en memoria: en serio guardalo en un repo/DB
  private final Map<Integer, String> tipoPorId = new HashMap<>();
  private static final int OFFSET = 1_000_000;
  private int idLocal(Integer id) { return id % OFFSET; }

  /* ===================== helpers ===================== */

  private static String trimTrailingSlash(String s) {
    return (s != null && s.endsWith("/")) ? s.substring(0, s.length() - 1) : s;
  }

  private static String join(String base, String suffix) {
    return (base.replaceAll("/+$","") + "/" + suffix.replaceAll("^/+",""));
  }

  private String intentarResolverTipoPorProbing(Integer id) {
    // id es local (sin prefijo)
    List<Map.Entry<String,String>> bases = List.of(
            Map.entry("DINAMICA", urlDinamica),
            Map.entry("ESTATICA", urlEstatica),
            Map.entry("PROXY",    urlProxy)
    );
    for (var e : bases) {
      String tipo = e.getKey();
      String base = e.getValue();
      if (base == null || base.isBlank()) continue;
      String url = join(base, "/" + id);
      try {
        ResponseEntity<Void> resp = restTemplate.exchange(url, HttpMethod.GET, null, Void.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
          tipoPorId.put(id, tipo); // cacheo
          return tipo;
        }
      } catch (Exception ignore) {}
    }
    return null;
  }


  private static String normalizarTipo(String tipoRaw) {
    if (tipoRaw == null) throw new IllegalArgumentException("Tipo nulo");
    String t = tipoRaw.trim().toUpperCase(Locale.ROOT)
            .replace("Á","A").replace("É","E").replace("Í","I").replace("Ó","O").replace("Ú","U");
    if (t.startsWith("FUENTE")) t = t.substring("FUENTE".length()); // quito prefijo “FUENTE”
    return t; // ESTATICA | DINAMICA | PROXY
  }

  private String endpointByTipo(String tipoRaw) {
    String t = normalizarTipo(tipoRaw);
    return switch (t) {
      case "ESTATICA" -> urlEstatica;
      case "DINAMICA" -> urlDinamica;
      case "PROXY"    -> urlProxy;
      default -> throw new IllegalArgumentException("Tipo inválido: " + tipoRaw);
    };
  }

  private String endpointById(Integer id) {
    String tipo = tipoPorId.get(id);

    if (tipo == null) {
      // a) si viene id con prefijo (>= 1_000_000), inferir por prefijo
      if (id >= OFFSET) {
        int prefijo = id / OFFSET;
        switch (prefijo) {
          case 1 -> tipo = "DINAMICA";
          case 2 -> tipo = "ESTATICA";
          case 3 -> tipo = "PROXY";
          default -> tipo = null;
        }
        if (tipo != null) {
          tipoPorId.put(id, tipo); // cachear ese id compuesto
        }
      } else {
        // b) id local: probar contra las tres bases
        tipo = intentarResolverTipoPorProbing(id);
      }
    }

    if (tipo == null) throw new IllegalStateException("No conozco el tipo de la fuente id=" + id);
    return endpointByTipo(tipo);
  }


  private void registrarFuenteLocal(Integer id, String tipoRaw) {
    tipoPorId.put(id, normalizarTipo(tipoRaw));
  }

  /* ===================== lecturas ===================== */

  public FuenteDeDatos getFuenteDeDatos(Integer id) {
    String base = endpointById(id);
    String url  = join(base, "/" + id); // …/api-fuentesDeDatos/{id}
    return restTemplate.getForObject(url, FuenteDeDatos.class);
  }

  public List<FuenteDeDatos> getFuentesDeDatos() {
    List<FuenteDeDatos> total = new ArrayList<>();
    total.addAll(fetchList(urlEstatica));
    total.addAll(fetchList(urlDinamica));
    total.addAll(fetchList(urlProxy));
    return total;
  }

  private static final Logger log = LoggerFactory.getLogger(ServiceFuenteDeDatos.class);

  private List<FuenteDeDatos> fetchList(String base) {
    if (base == null || base.isBlank()) return List.of();
    String url = base.endsWith("/") ? base : base + "/";
    try {
      ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
      String body = resp.getBody();
      log.info("GET {} -> {} | body[0..500]: {}", url, resp.getStatusCode(),
              body == null ? "null" : body.substring(0, Math.min(500, body.length())));

      if (!resp.getStatusCode().is2xxSuccessful() || body == null || body.isBlank()) return List.of();

      return parseFuentesFlex(body); // ver sección 2

    } catch (Exception e) {
      log.error("Error llamando {}: {}", url, e.toString(), e);
      return List.of();
    }
  }

  private List<FuenteDeDatos> parseFuentesFlex(String body) throws IOException {
    ObjectMapper om = objectMapper.copy()
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    JsonNode root = om.readTree(body);

    // Caso 1: ya es array
    if (root.isArray()) {
      var listType = om.getTypeFactory().constructCollectionType(List.class, FuenteDeDatos.class);
      return om.readerFor(listType).readValue(body);
    }

    // Caso 2: objeto único -> lo tratamos como lista de uno
    if (root.isObject()) {
      // Wrappers típicos
      for (String key : List.of("items","data","content","results","list","fuentes","sources")) {
        JsonNode arr = root.get(key);
        if (arr != null) {
          if (arr.isArray()) {
            var listType = om.getTypeFactory().constructCollectionType(List.class, FuenteDeDatos.class);
            return om.readerFor(listType).readValue(arr.traverse());
          } else if (arr.isObject()) {
            FuenteDeDatos uno = om.treeToValue(arr, FuenteDeDatos.class);
            return uno == null ? List.of() : List.of(uno);
          }
        }
      }
      // Si no hay wrapper, intentar mapear el objeto como una sola fuente
      FuenteDeDatos uno = om.treeToValue(root, FuenteDeDatos.class);
      return uno == null ? List.of() : List.of(uno);
    }

    // Cualquier otro caso (texto/HTML/etc.)
    return List.of();
  }

  /* ===================== escrituras ===================== */

  // Crear fuente mandando JSON crudo (desde tu Controller Opción C)
  public ResponseEntity<String> crearFuente(String jsonPayload) {
    String tipo = extraerTipo(jsonPayload);            // p.ej. "FUENTEESTATICA" o "ESTATICA"
    String endpoint = endpointByTipo(tipo);            // 9002/9001/9003
    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<String> resp = restTemplate.exchange(
            join(endpoint, "/"),                            // muchos POST requieren el slash final
            HttpMethod.POST,
            new HttpEntity<>(jsonPayload, h),
            String.class
    );

    // Intento registrar id->tipo si el body trae {"id": ...}
    try {
      if (resp.getBody() != null && !resp.getBody().isBlank()) {
        JsonNode root = objectMapper.readTree(resp.getBody());
        if (root.hasNonNull("id")) {
          registrarFuenteLocal(root.get("id").asInt(), tipo);
        }
      }
    } catch (Exception ignore) {}

    return resp;
  }

  private String extraerTipo(String jsonPayload) {
    try {
      JsonNode root = objectMapper.readTree(jsonPayload);
      JsonNode tipoNode = root.get("tipo");
      if (tipoNode == null || tipoNode.isNull()) throw new IllegalArgumentException("Falta 'tipo'");
      return tipoNode.asText();
    } catch (Exception e) {
      throw new RuntimeException("No se pudo parsear 'tipo' del JSON", e);
    }
  }

  // Crear fuente con DTO simple
  public Integer crearFuenteYRetornarId(String tipo, String nombre, String url) {
    String t = normalizarTipo(tipo); // -> ESTATICA | DINAMICA | PROXY
    String endpoint = endpointByTipo(tipo);

    Map<String,Object> payload = new HashMap<>();

    switch (t) {
      case "ESTATICA" -> {
        // Tu Controller Estática pide solo "nombre"
        payload.put("nombre", nombre);
      }
      case "DINAMICA" -> {
        // Tu Controller Dinámica ignora el body; opcionalmente mandamos nombre
        if (nombre != null && !nombre.isBlank()) {
          payload.put("nombre", nombre);
        }
        // NO mandamos tipo/url
      }
      case "PROXY" -> {
        // Controller Proxy recibe Map y el service valida; cubrimos ambos nombres de campo
        payload.put("nombre", nombre);
        if (url != null && !url.isBlank()) {
          payload.put("proxyUrl", url); // nombre común en loaders proxy
          payload.put("url", url);      // por si tu service espera "url"
        }
      }
      default -> throw new IllegalArgumentException("Tipo inválido: " + tipo);
    }

    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);
    h.setAccept(List.of(MediaType.APPLICATION_JSON));

    ResponseEntity<Map> resp = restTemplate.exchange(
            join(endpoint, "/"),
            HttpMethod.POST,
            new HttpEntity<>(payload, h),
            Map.class
    );

    // --- extracción robusta del ID ---
    Map body = resp.getBody();
    Integer idNum = tryExtractId(body, "id");        // caso común
    if (idNum == null) idNum = tryExtractId(body, "fuenteId");  // p.ej. FuenteDinamica
    if (idNum == null) idNum = tryExtractId(body, "idFuente");  // otro alias posible

    // Fallback: parsear desde Location: .../fuentesDeDatos/{id}
    if (idNum == null && resp.getHeaders().getLocation() != null) {
      String path = resp.getHeaders().getLocation().getPath();
      String last = path.substring(path.lastIndexOf('/') + 1);
      try { idNum = Integer.parseInt(last); } catch (NumberFormatException ignored) {}
    }

    if (idNum == null) {
      throw new IllegalStateException("No se pudo obtener el id de la fuente. status="
              + resp.getStatusCode()
              + " location=" + resp.getHeaders().getLocation()
              + " body=" + body);
    }

    registrarFuenteLocal(idNum, tipo);
    return idNum;
  }

// Helpers

  private Integer tryExtractId(Map body, String key) {
    if (body == null) return null;
    Object v = body.get(key);
    if (v == null) return null;
    if (v instanceof Number) return ((Number) v).intValue();
    try { return Integer.parseInt(v.toString()); } catch (Exception e) { return null; }
  }

  public Integer cargarHecho(Integer idFuenteDeDatos,
                             String titulo,
                             String descripcion,
                             String categoria,
                             Float latitud,
                             Float longitud,
                             LocalDate fechaHecho,
                             String autor,
                             Boolean anonimo,
                             List<Multimedia> multimedia) {

    if (titulo == null || titulo.isBlank()) {
      throw new IllegalArgumentException("El título es obligatorio.");
    }
    if ((latitud == null) ^ (longitud == null)) {
      throw new IllegalArgumentException("Si enviás coordenadas, incluí latitud y longitud.");
    }

    String base = endpointById(idFuenteDeDatos);
    String url  = join(base, "/" + idFuenteDeDatos + "/hechos"); // <- usa el id COMPLETO que te pasaron

    boolean an = anonimo != null && anonimo;
    String t  = titulo.trim();
    String d  = (descripcion == null || descripcion.isBlank()) ? null : descripcion.trim();
    String c  = (categoria == null || categoria.isBlank()) ? null : categoria.trim();
    String au = an ? null : ((autor == null || autor.isBlank()) ? null : autor.trim());

    Map<String,Object> payload = new HashMap<>();
    payload.put("titulo", t);
    if (d != null) payload.put("descripcion", d);
    if (c != null) payload.put("categoria", c);
    if (latitud != null)  payload.put("latitud",  latitud);
    if (longitud != null) payload.put("longitud", longitud);
    if (fechaHecho != null) payload.put("fechaHecho", fechaHecho.toString()); // yyyy-MM-dd
    if (au != null) payload.put("autor", au);
    payload.put("anonimo", an);

    if (multimedia != null && !multimedia.isEmpty()) {
      List<Map<String,Object>> mm = multimedia.stream()
              .filter(m -> m != null && m.getTipoMultimedia() != null && m.getPath() != null && !m.getPath().isBlank())
              .map(m -> {
                Map<String,Object> e = new HashMap<>();
                e.put("tipoMultimedia", m.getTipoMultimedia().name());
                e.put("path", m.getPath().trim());
                return e;
              }).collect(Collectors.toList());
      if (!mm.isEmpty()) payload.put("multimedia", mm);
    }

    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);

    @SuppressWarnings("unchecked")
    Map<String,Object> resp = restTemplate.postForObject(url, new HttpEntity<>(payload, h), Map.class);

    if (resp == null) {
      throw new IllegalStateException("Respuesta vacía del servicio de la fuente al cargar el hecho.");
    }

// Acepto varias claves posibles y doy un error claro si no viene ninguna:
    Object idObj = (resp.containsKey("id") ? resp.get("id")
            : resp.containsKey("hechoId") ? resp.get("hechoId")
            : resp.containsKey("idHecho") ? resp.get("idHecho")
            : null);

    if (idObj == null) {
      throw new IllegalStateException("La respuesta no incluyó un 'id' de hecho. Body=" + resp);
    }

    return (idObj instanceof Number) ? ((Number) idObj).intValue()
            : Integer.parseInt(idObj.toString());

  }



  public void cargarCSV(Integer id, MultipartFile file) throws IOException {
    String base = endpointById(id);
    String url  = join(base, "/" + id + "/cargarCSV");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    InputStreamResource resource = new InputStreamResource(file.getInputStream()) {
      @Override public String getFilename() { return file.getOriginalFilename(); }
      @Override public long contentLength() { return file.getSize(); }
    };

    // Parte de archivo con Content-Disposition correcto
    HttpHeaders fileHeaders = new HttpHeaders();
    fileHeaders.setContentDisposition(ContentDisposition.formData()
            .name("file").filename(resource.getFilename()).build());

    MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
    body.add("file", new HttpEntity<>(resource, fileHeaders));

    restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Void.class);
  }
}
