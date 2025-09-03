package Metamapa.service;

import Metamapa.business.Colecciones.Coleccion;
import Metamapa.business.Colecciones.Criterio;
import Metamapa.business.Consenso.Consenso;
import Metamapa.business.Consenso.ModosDeNavegacion;
import Metamapa.business.Hechos.Hecho;
import Metamapa.business.Hechos.TipoMultimedia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceColecciones {
  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceColecciones(RestTemplate restTemplate,
                            @Value("${M.Agregador.Service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

    public List<Coleccion> getColecciones() {
        ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
                baseUrl + "/api-colecciones/", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String,Object>>>() {}
        );
        List<Map<String,Object>> rows = resp.getBody();
        if (rows == null) return List.of();
        return rows.stream().map(this::toColeccion).collect(Collectors.toList());
    }

    public Coleccion getColeccion(UUID id) {
        ResponseEntity<Map<String,Object>> resp = restTemplate.exchange(
                baseUrl + "/api-colecciones/" + id, HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String,Object>>() {}
        );
        Map<String,Object> row = resp.getBody();
        return (row == null) ? null : toColeccion(row);
    }

    public ArrayList<Hecho> getHechosDeColeccion(UUID id) {
      ResponseEntity<Map<String,Object>> resp = restTemplate.exchange(
              baseUrl + "/api-colecciones/" + id, HttpMethod.GET, null,
              new ParameterizedTypeReference<Map<String,Object>>() {}
      );
      Map<String,Object> row = resp.getBody();
      Coleccion coleccion = (row == null) ? null : toColeccion(row);
      return coleccion.getAgregador().getListaDeHechos();
    }

  public UUID crearColeccion(String titulo, String descripcion, String consenso,
                             List<Map<String, Object>> pertenencia,
                             List<Map<String, Object>> noPertenencia) {
    // 1) Armar payload seguro (listas vacías si vienen null)
    Map<String, Object> payload = new HashMap<>();
    payload.put("titulo", titulo);
    payload.put("descripcion", descripcion);
    payload.put("consenso", consenso); // "Absoluto" | "MayoriaSimple" | "MultiplesMenciones"
    payload.put("criteriosPertenencia", pertenencia == null ? List.of() : pertenencia);
    payload.put("criteriosNoPertenencia", noPertenencia == null ? List.of() : noPertenencia);

    // Log rápido para verificar qué se envía
    System.out.println("CREAR → payload = " + payload);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

    // 2) Construir URL sin riesgo de dobles barras
    String url = baseUrl.replaceAll("/+$", "") + "/api-colecciones/";

    // 3) POST y lectura tolerante de id/handle
    Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
    System.out.println("CREAR ← respuesta = " + response);

    if (response == null) {
      throw new IllegalStateException("Respuesta nula del Agregador");
    }
    Object idLike = response.get("id");
    if (idLike == null) idLike = response.get("handle"); // por si el DTO devuelve 'handle'
    if (idLike == null) {
      throw new IllegalStateException("Respuesta sin 'id' ni 'handle': " + response);
    }

    return UUID.fromString(idLike.toString());
  }

    public HttpStatus deleteColeccion(UUID uuid) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl.replaceAll("/+$",""))
                .pathSegment("api-colecciones", uuid.toString())
                .build()
                .toUri();

        try {
            ResponseEntity<Void> resp = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);
            return (HttpStatus) resp.getStatusCode(); // 204 si salió bien
        } catch (HttpStatusCodeException e) {
            System.err.println("Error al eliminar la colección: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            return (HttpStatus) e.getStatusCode(); // 404, 405, etc.
        } catch (Exception e) {
            System.err.println("Error al eliminar la colección: " + e.getMessage());
            return HttpStatus.BAD_GATEWAY; // o INTERNAL_SERVER_ERROR
        }
  }

    public HttpStatus actualizarAlgoritmoConsenso(UUID uuid, String algoritmo) {
        // Construir URL correcta: /api-colecciones/{id}
        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl.replaceAll("/+$",""))
                .pathSegment("api-colecciones", uuid.toString())
                .build()
                .toUri();

        // Body JSON con el algoritmo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
      var body = java.util.Map.of("consenso", algoritmo); // <-- antes decía "algoritmo"
        var entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> resp = restTemplate.exchange(uri, HttpMethod.PATCH, entity, Void.class);
            return (HttpStatus) resp.getStatusCode(); // 200/204 según tu Agregador
        } catch (HttpStatusCodeException e) {
            System.err.println("PATCH consenso error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            return (HttpStatus) e.getStatusCode();
        } catch (Exception e) {
            System.err.println("PATCH consenso error: " + e.getMessage());
            return HttpStatus.BAD_GATEWAY;
        }
    }

  public List<Hecho> navegarFiltrado(
            UUID idColeccion,
            ModosDeNavegacion modo,                // CURADA o IRRESTRICTA
            String categoria,
            String titulo,
            String descripcion,
            LocalDate fechaReporteDesde,
            LocalDate fechaReporteHasta,
            LocalDate fechaAcontecimientoDesde,
            LocalDate fechaAcontecimientoHasta,
            Float ubicacionLatitud,
            Float ubicacionLongitud,
            Double radioKm,                         // criterio por radio
            Integer idFuente,
            TipoMultimedia tipoMultimedia
    ) {
        // OJO: esta ruta debe coincidir con tu controller del Agregador
        // Ej: @GetMapping("/metamapa/api/colecciones/{idColeccion}/hechos")
        String base = String.format("%s/metamapa/api/colecciones/%s/hechos", baseUrl, idColeccion);

        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(base)
                .queryParam("modo", modo != null ? modo.name() : ModosDeNavegacion.CURADA.name());

        add(uri, "categoria", categoria);
        add(uri, "titulo", titulo);
        add(uri, "descripcion", descripcion);

        addDate(uri, "fecha_reporte_desde", fechaReporteDesde);
        addDate(uri, "fecha_reporte_hasta", fechaReporteHasta);
        addDate(uri, "fecha_acontecimiento_desde", fechaAcontecimientoDesde);
        addDate(uri, "fecha_acontecimiento_hasta", fechaAcontecimientoHasta);

        addNum(uri, "ubicacion_latitud", ubicacionLatitud);
        addNum(uri, "ubicacion_longitud", ubicacionLongitud);
        addNum(uri, "radio_km", radioKm);

        if (idFuente != null) uri.queryParam("id_fuente", idFuente);
        if (tipoMultimedia != null) uri.queryParam("tipo_multimedia", tipoMultimedia.name());

        ResponseEntity<List<Hecho>> resp = restTemplate.exchange(
                uri.build(true).toUri(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<Hecho>>() {}
        );

        return resp.getBody() != null ? resp.getBody() : Collections.emptyList();
    }
    private void add(UriComponentsBuilder uri, String key, String val) {
        if (val != null && !val.isBlank()) uri.queryParam(key, val.trim());
    }
    private void addDate(UriComponentsBuilder uri, String key, LocalDate d) {
        if (d != null) uri.queryParam(key, d.toString()); // ISO-8601: yyyy-MM-dd
    }
    private void addNum(UriComponentsBuilder uri, String key, Number n) {
        if (n != null) uri.queryParam(key, n);
    }

    // ---------- mapping JSON (Agregador) -> dominio (MetaMapa) ----------
    private Coleccion toColeccion(Map<String, Object> row) {
        String titulo = (String) row.get("titulo");
        String descripcion = (String) row.get("descripcion");
        String consensoStr = (String) row.get("consenso");
        Consenso consenso = Consenso.fromString(consensoStr);

        // handle/id
        String handleStr = (String) (row.get("handle") != null ? row.get("handle") : row.get("id"));
        UUID handle = (handleStr != null && !handleStr.isBlank()) ? UUID.fromString(handleStr) : null;

        // criterios (si tus endpoints los devuelven; si no, dejá listas vacías)
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> critP = (List<Map<String, Object>>) row.get("criteriosPertenencia");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> critNP = (List<Map<String, Object>>) row.get("criteriosNoPertenencia");

        ArrayList<Criterio> pert = mapCriterios(critP);
        ArrayList<Criterio> noPert = mapCriterios(critNP);

        if (handle != null) {
            return new Coleccion(titulo, descripcion, consenso, pert, noPert);
        }
        return new Coleccion(titulo, descripcion, consenso, pert, noPert);
    }

    private ArrayList<Criterio> mapCriterios(List<Map<String, Object>> crudos) {
        ArrayList<Criterio> out = new ArrayList<>();
        if (crudos == null) return out;
        for (Map<String, Object> c : crudos) {
            // ejemplo básico por "tipo" -> creá tus Criterio* reales acá
            String tipo = (String) c.get("tipo"); // p.ej. "titulo", "fuente", etc.
            Object valor = c.get("valor");
            // TODO: construir la subclase correcta de Criterio en base a tipo/valor
            // out.add(new CriterioTitulo((String) valor));
        }
        return out;
    }

}