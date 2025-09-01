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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    Map<String, Object> response = restTemplate.postForObject(
            baseUrl + "/api-colecciones/",
            request,
            Map.class
    );
      String handleStr = (String) Objects.requireNonNull(response).get("handle");
      return UUID.fromString(handleStr);
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