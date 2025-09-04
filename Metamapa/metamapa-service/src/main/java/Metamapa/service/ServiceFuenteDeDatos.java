package Metamapa.service;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.http.client.ClientHttpResponse;
import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.FuentesDeDatos.FuenteDinamica;
import Metamapa.business.FuentesDeDatos.FuenteEstatica;
import Metamapa.business.FuentesDeDatos.FuenteProxy;
import Metamapa.business.Hechos.Multimedia;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

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
  private static final Logger log = LoggerFactory.getLogger(ServiceFuenteDeDatos.class);
  private static final int OFFSET = 1_000_000;

  public String getFuenteDeDatosRaw(Integer id) {
    String base = resolverBaseUrl(id);
    String url  = join(base, "/" + id);   // usamos el id prefijado que vos mismo generás (1000001)
    log.info("Proxy GET one -> {}", url);
    return httpGetRaw(url);                // devuelve JSON crudo (String)
  }

  public String getFuentesDeDatosRaw() {
    List<String> bases = List.of(urlEstatica, urlDinamica, urlProxy);
    ArrayNode union = objectMapper.createArrayNode();

    for (String base : bases) {
      if (base == null || base.isBlank()) continue;
      String url = base.endsWith("/") ? base : base + "/";
      try {
        String body = httpGetRaw(url);
        JsonNode n = objectMapper.readTree(body);
        if (n.isArray()) {
          n.forEach(union::add);
        } else if (n.isObject()) {
          union.add(n);
        }
      } catch (Exception e) {
        log.warn("Listado falló en {}: {}", url, e.toString());
      }
    }
    // devolvemos un único array JSON concatenando lo de todos los micros
    return union.toString();
  }

  // === Helper: GET crudo como String (sin Jackson / converters) ===
  private String httpGetRaw(String url) {
    return restTemplate.execute(
            url,
            HttpMethod.GET,
            req -> {
              // Acepto cualquier cosa para que NO elija el converter de JSON
              req.getHeaders().setAccept(List.of(MediaType.ALL));
            },
            (ClientHttpResponse resp) -> {
              var status = resp.getStatusCode();
              if (!status.is2xxSuccessful()) {
                throw new RestClientException("GET " + url + " -> " + status);
              }
              try (var in = resp.getBody()) {
                if (in == null) return null;
                byte[] bytes = in.readAllBytes();
                return new String(bytes, StandardCharsets.UTF_8);
              }
            }
    );
  }

  public FuenteDeDatos getFuenteDeDatos(Integer id) {
    String base = resolverBaseUrl(id);      // ej: http://localhost:9001/api-fuentesDeDatos
    int idLocal = id % OFFSET;              // 1000001 -> 1

    List<String> candidates = new ArrayList<>();
    candidates.add(join(base, "/" + id));           // …/api-fuentesDeDatos/1000001
    if (idLocal != 0) candidates.add(join(base, "/" + idLocal)); // …/api-fuentesDeDatos/1

    log.warn("Intentos para id={} en base={}: {}", id, base, String.join(" | ", candidates));

    // 1) Intentos directos (prefijado y local)
    for (String url : candidates) {
      try {
        String body = httpGetRaw(url);  // 👈 leer crudo
        log.info("GET {} OK | body[0..300]: {}",
                url, body == null ? "null" : body.substring(0, Math.min(300, body.length())));
        if (body == null || body.isBlank()) continue;
        FuenteDeDatos f = parseFuenteFlex(body);
        if (f != null) return f;
      } catch (Exception e) {
        log.warn("GET {} falló: {}", url, e.toString());
      }
    }

// Fallback: listar
    try {
      List<FuenteDeDatos> todas = fetchList(base);
      // ... (tu mismo código de búsqueda por id / idLocal)
    } catch (Exception e) {
      log.warn("Fallback (listar) falló en {}: {}", base, e.toString());
    }

    throw new NoSuchElementException("No se pudo recuperar la fuente id=" + id + " (ni con id completo ni local).");
  }

  /* ===================== helpers ===================== */

  private static String trimTrailingSlash(String s) {
    return (s != null && s.endsWith("/")) ? s.substring(0, s.length() - 1) : s;
  }

  private static String join(String base, String suffix) {
    return (base.replaceAll("/+$","") + "/" + suffix.replaceAll("^/+",""));
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

  private String resolverBaseUrl(Integer idFuente) {
    int tipo = idFuente / OFFSET; // 1=dinámica, 2=estática, 3=proxy
    return switch (tipo) {
      case 1 -> urlDinamica;
      case 2 -> urlEstatica;
      case 3 -> urlProxy;
      default -> throw new IllegalArgumentException("Tipo de fuente desconocido para id=" + idFuente);
    };
  }

  private String tipoPorBase(String base) {
    String b = base == null ? "" : base.replaceAll("/+$", "");
    if (b.equals(urlDinamica)) return "FUENTEDINAMICA";
    if (b.equals(urlEstatica)) return "FUENTEESTATICA";
    if (b.equals(urlProxy))    return "FUENTEPROXY";
    return "FUENTEDINAMICA"; // fallback razonable
  }

  /* ===================== lecturas ===================== */

  public List<FuenteDeDatos> getFuentesDeDatos() {
    List<FuenteDeDatos> total = new ArrayList<>();
    total.addAll(fetchList(urlEstatica));
    total.addAll(fetchList(urlDinamica));
    total.addAll(fetchList(urlProxy));
    return total;
  }

  private List<FuenteDeDatos> fetchList(String base) {
    if (base == null || base.isBlank()) return List.of();

    String url = base.endsWith("/") ? base : base + "/";
    String defaultTipo = tipoPorBase(base);

    try {
      String body = httpGetRaw(url);  // <- lee crudo, sin converters
      log.info("GET {} -> body[0..500]: {}", url,
              body == null ? "null" : body.substring(0, Math.min(500, body.length())));
      if (body == null || body.isBlank()) return List.of();

      return parseFuentesFlex(body, defaultTipo); // 👈 ahora con default
    } catch (Exception e) {
      log.error("Error llamando {}: {}", url, e.toString(), e);
      return List.of();
    }
  }


  private List<FuenteDeDatos> parseFuentesFlex(String body, String defaultTipo) throws IOException {
    ObjectMapper om = objectMapper.copy()
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    JsonNode root = om.readTree(body);

    // Caso 1: array "puro"
    if (root.isArray()) {
      ArrayNode arr = (ArrayNode) root;
      ArrayNode mod = om.createArrayNode();
      for (JsonNode n : arr) {
        if (!n.isObject()) continue;  // ignoro raros
        ObjectNode on = (ObjectNode) n.deepCopy();
        asegurarTipoFuente(on, defaultTipo); // 👈 inyecta/normaliza
        mod.add(on);
      }
      var listType = om.getTypeFactory().constructCollectionType(List.class, FuenteDeDatos.class);
      return om.readerFor(listType).readValue(mod.traverse());
    }

    // Caso 2: wrappers típicos
    if (root.isObject()) {
      for (String key : List.of("items","data","content","results","list","fuentes","sources")) {
        JsonNode arr = root.get(key);
        if (arr != null && arr.isArray()) {
          ArrayNode mod = om.createArrayNode();
          for (JsonNode n : arr) {
            if (!n.isObject()) continue;
            ObjectNode on = (ObjectNode) n.deepCopy();
            asegurarTipoFuente(on, defaultTipo);
            mod.add(on);
          }
          var listType = om.getTypeFactory().constructCollectionType(List.class, FuenteDeDatos.class);
          return om.readerFor(listType).readValue(mod.traverse());
        }
      }
      // Si no hay wrapper, tratar como objeto único
      ObjectNode on = (ObjectNode) root.deepCopy();
      asegurarTipoFuente(on, defaultTipo);
      FuenteDeDatos uno = objectMapper
              .copy()
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
              .readValue(on.traverse(), FuenteDeDatos.class);
      return (uno == null) ? List.of() : List.of(uno);
    }

    // Cualquier otro caso (texto/HTML/etc.)
    return List.of();
  }


  /* ===================== escrituras ===================== */

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

  private FuenteDeDatos parseFuenteFlex(String body) {
    ObjectMapper om = objectMapper.copy()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try {
      JsonNode n = om.readTree(body);

      // Si vino array por error, uso el primero
      if (n.isArray()) {
        if (n.size() == 0) return null;
        n = n.get(0);
      }
      if (!n.isObject()) throw new IllegalStateException("JSON inesperado (no es objeto).");

      ObjectNode on = (ObjectNode) n;

      // Inyectar/normalizar tipoFuente (permite polimorfismo aunque el upstream no lo envíe)
      String raw  = on.hasNonNull("tipoFuente") ? on.get("tipoFuente").asText() : null;
      String tipo = normalizarTipoParaJson(raw); // FUENTEDEMO / FUENTEESTATICA / FUENTEDINAMICA / FUENTEMETAMAPA / FUENTEPROXY
      on.put("tipoFuente", tipo);

      // Polimórfico "limpio": que Jackson resuelva con @JsonTypeInfo/@JsonSubTypes
      return om.readValue(on.traverse(), FuenteDeDatos.class);

    } catch (Exception e) {
      throw new IllegalStateException("Parse polimórfico falló: " + e.getMessage(), e);
    }
  }

  private static String normalizarTipoParaJson(String raw) {
    if (raw == null) return "FUENTEDINAMICA";
    String r = raw.trim().toUpperCase(Locale.ROOT);
    return switch (r) {
      case "FUENTEDEMO", "FUENTE DEMO", "DEMO"           -> "FUENTEDEMO";
      case "FUENTEDINAMICA", "DINAMICA", "FUENTE DINAMICA" -> "FUENTEDINAMICA";
      case "FUENTEESTATICA", "ESTATICA", "FUENTE ESTATICA" -> "FUENTEESTATICA";
      case "FUENTEPROXY", "PROXY", "FUENTE PROXY"         -> "FUENTEPROXY";
      case "FUENTEMETAMAPA", "METAMAPA", "FUENTE METAMAPA" -> "FUENTEMETAMAPA";
      default -> "FUENTEDINAMICA";
    };
  }

  private void asegurarTipoFuente(ObjectNode on, String defaultTipo) {
    // Si ya viene "tipoFuente", lo normalizo y listo
    if (on.hasNonNull("tipoFuente")) {
      String tf = normalizarTipoParaJson(on.get("tipoFuente").asText());
      on.put("tipoFuente", tf);
      return;
    }
    // Si viene "tipo", úsalo para derivar "tipoFuente"
    if (on.hasNonNull("tipo")) {
      String tf = normalizarTipoParaJson(on.get("tipo").asText());
      on.put("tipoFuente", tf);
      return;
    }
    // Si no vino nada, usa el default por base
    on.put("tipoFuente", normalizarTipoParaJson(defaultTipo));
  }


  // Crear fuente con DTO simple
// Crear fuente con DTO simple
  public Integer crearFuenteYRetornarId(String tipo, String nombre, String url) {
    String t = normalizarTipo(tipo);          // -> ESTATICA | DINAMICA | PROXY
    String endpoint = endpointByTipo(t);      // base del micro correcto

    Map<String,Object> payload = new HashMap<>();
    switch (t) {
      case "ESTATICA" -> payload.put("nombre", nombre);
      case "DINAMICA" -> { if (nombre != null && !nombre.isBlank()) payload.put("nombre", nombre); }
      case "PROXY" -> {
        payload.put("nombre", nombre);
        if (url != null && !url.isBlank()) {
          payload.put("proxyUrl", url);
          payload.put("url", url);
        }
      }
      default -> throw new IllegalArgumentException("Tipo inválido: " + tipo);
    }

    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_JSON);
    h.setAccept(List.of(MediaType.APPLICATION_JSON));

    ResponseEntity<Map> resp = restTemplate.exchange(
            join(endpoint, "/"),                  // muchos POST requieren el slash final
            HttpMethod.POST,
            new HttpEntity<>(payload, h),
            Map.class
    );

    Map body = resp.getBody();
    Integer idNum = tryExtractId(body, "id");
    if (idNum == null) idNum = tryExtractId(body, "fuenteId");
    if (idNum == null) idNum = tryExtractId(body, "idFuente");

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

    // ⚠️ Se eliminó registrarFuenteLocal(idNum, t);
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

    String base = resolverBaseUrl(idFuenteDeDatos);
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
    String base = resolverBaseUrl(id);
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

  // NUEVO: devuelve una lista “plana” para la tabla (sin Jackson polimórfico)
  public List<Map<String,Object>> getFuentesTabla() {
    List<Map<String,Object>> out = new ArrayList<>();
    for (String base : List.of(urlEstatica, urlDinamica, urlProxy)) {
      if (base == null || base.isBlank()) continue;
      String url = base.endsWith("/") ? base : base + "/";
      String defaultTipo = tipoPorBase(base); // "FUENTEDINAMICA" etc.

      try {
        String body = httpGetRaw(url);
        if (body == null || body.isBlank()) continue;

        // Parseo genérico a List/Map, sin usar FuenteDeDatos
        ObjectMapper om = objectMapper;
        JsonNode root = om.readTree(body);

        if (root.isArray()) {
          for (JsonNode n : root) out.add(aFila(n, defaultTipo));
        } else if (root.isObject()) {
          out.add(aFila(root, defaultTipo));
        }
      } catch (Exception e) {
        log.warn("getFuentesTabla: fallo listando {}: {}", url, e.toString());
      }
    }
    // Orden por id si existe
    out.sort(Comparator.comparing(m -> {
      Object v = m.getOrDefault("id", Integer.MAX_VALUE);
      try { return Integer.valueOf(v.toString()); } catch (Exception e) { return Integer.MAX_VALUE; }
    }));
    return out;
  }

  // Helpers para “aplanar” un item a lo que la tabla necesita
  private Map<String,Object> aFila(JsonNode n, String defaultTipo) {
    Map<String,Object> row = new LinkedHashMap<>();
    if (n == null || !n.isObject()) return row;

    row.put("id",   opt(n,"id",  opt(n,"fuenteId", opt(n,"idFuente", null))));
    row.put("nombre", opt(n,"nombre", "(sin nombre)"));

    // tipoFuente if present, sino "tipo", sino el default de la base
    String tipo = String.valueOf(
            opt(n, "tipoFuente",
                    opt(n, "tipo", defaultTipo))
    );
    row.put("tipoFuente", tipo);
    return row;
  }

  private Object opt(JsonNode n, String k, Object def) {
    JsonNode j = n.get(k);
    if (j == null || j.isNull()) return def;
    if (j.isNumber()) return j.numberValue();
    if (j.isBoolean()) return j.booleanValue();
    return j.asText();
  }

}
