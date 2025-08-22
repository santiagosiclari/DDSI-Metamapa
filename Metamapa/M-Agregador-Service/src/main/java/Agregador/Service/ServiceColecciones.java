package Agregador.Service;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.*;
import Agregador.persistencia.RepositorioColecciones;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceColecciones {
  public final RepositorioColecciones repositorioColecciones;

  public ServiceColecciones(RepositorioColecciones repositorioColecciones) {
    this.repositorioColecciones = repositorioColecciones;
  }

  public ArrayList<Hecho> getHechosColeccion(UUID identificador, String modoNavegacion, Map<String, String> paramsP, Map<String, String> paramsNP) {
    Coleccion coleccion = repositorioColecciones.buscarXUUID(identificador);
    ModosDeNavegacion modo = ModosDeNavegacion.valueOf(modoNavegacion.toUpperCase());
    ArrayList<Criterio> criteriosP = (ArrayList<Criterio>) procesarCriterios(
            construirCriteriosJson(
                    paramsP.get("titulo"),
                    paramsP.get("descripcion"),
                    paramsP.get("categoria"),
                    paramsP.get("fecha_reporte_desde"),
                    paramsP.get("fecha_reporte_hasta"),
                    paramsP.get("fecha_acontecimiento_desde"),
                    paramsP.get("fecha_acontecimiento_hasta"),
                    paramsP.get("latitud"),
                    paramsP.get("longitud"),
                    paramsP.get("tipoMultimedia")
            )
    );
    ArrayList<Criterio> criteriosNP = (ArrayList<Criterio>) procesarCriterios(
            construirCriteriosJson(
                    paramsNP.get("titulo"),
                    paramsNP.get("descripcion"),
                    paramsNP.get("categoria"),
                    paramsNP.get("fecha_reporte_desde"),
                    paramsNP.get("fecha_reporte_hasta"),
                    paramsNP.get("fecha_acontecimiento_desde"),
                    paramsNP.get("fecha_acontecimiento_hasta"),
                    paramsNP.get("latitud"),
                    paramsNP.get("longitud"),
                    paramsNP.get("tipoMultimedia")
            )
    );
    // TODO: implementar la obtención de hechos
    // return coleccion.filtrarPorCriterios(serviceAgregador.getAgregadorHechos(), criteriosP, criteriosNP, modo);
    return new ArrayList<>();
  }

  public ArrayList<Coleccion> obtenerTodasLasColecciones() {
    return repositorioColecciones.getColecciones();
  }

  public Coleccion obtenerColeccionPorId(UUID id) {
    return repositorioColecciones.buscarXUUID(id);
  }

  public ResponseEntity<?> crearColeccion(Map<String, Object> requestBody) {
    String titulo = (String) requestBody.get("titulo");
    if (titulo == null || titulo.isBlank())
      return ResponseEntity.badRequest().body(Map.of("error", "El campo 'titulo' es obligatorio"));
    String descripcion = (String) requestBody.get("descripcion");
    List<Criterio> criteriosPertenencia = parseCriterios(requestBody.get("criteriosPertenencia"));
    List<Criterio> criteriosNoPertenencia = parseCriterios(requestBody.get("criteriosNoPertenencia"));
    Coleccion coleccion = new Coleccion(titulo, descripcion, (ArrayList<Criterio>) criteriosPertenencia, (ArrayList<Criterio>) criteriosNoPertenencia);
    Optional.ofNullable(requestBody.get("consenso"))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(Consenso::stringToConsenso)
            .ifPresent(coleccion::setConsenso);
    repositorioColecciones.getColecciones().add(coleccion);
    return ResponseEntity.status(201).body(coleccion);
  }

  public ResponseEntity<?> actualizarColeccion(UUID id, Map<String, Object> requestBody) {
    Coleccion coleccion = repositorioColecciones.buscarXUUID(id);
    /* if (coleccionOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    Coleccion coleccion = coleccionOpt.get();*/
    if (requestBody.containsKey("titulo")) coleccion.setTitulo((String) requestBody.get("titulo"));
    if (requestBody.containsKey("descripcion")) coleccion.setDescripcion((String) requestBody.get("descripcion"));
    if (requestBody.containsKey("consenso")) {
      String consensoStr = (String) requestBody.get("consenso");
      coleccion.setConsenso(Consenso.stringToConsenso(consensoStr));
    }
    if (requestBody.containsKey("criteriosPertenencia")) {
      List<Map<String, Object>> criteriosData = (List<Map<String, Object>>) requestBody.get("criteriosPertenencia");
      List<Criterio> nuevosCriterios = criteriosData.stream()
              .map(this::crearCriterioDesdeJson)
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
      coleccion.setCriterioPertenencia((ArrayList<Criterio>) nuevosCriterios);
    }
    if (requestBody.containsKey("criteriosNoPertenencia")) {
      List<Map<String, Object>> criteriosData = (List<Map<String, Object>>) requestBody.get("criteriosNoPertenencia");
      List<Criterio> nuevosCriterios = criteriosData.stream()
              .map(this::crearCriterioDesdeJson)
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
      coleccion.setCriterioNoPertenencia((ArrayList<Criterio>) nuevosCriterios);
    }
    return ResponseEntity.ok(coleccion);
  }

  public ResponseEntity<?> modificarAlgoritmo(UUID id, Map<String, Object> requestBody) {
    Coleccion coleccion = repositorioColecciones.buscarXUUID(id);
    if (coleccion == null)
      return ResponseEntity.status(404).body(Map.of("error", "Colección no encontrada"));
    if (requestBody.containsKey("consenso")) {
      String consensoStr = (String) requestBody.get("consenso");
      coleccion.setConsenso(Consenso.stringToConsenso(consensoStr));
    } else {
      return ResponseEntity.badRequest().body(Map.of("error", "El campo 'consenso' es obligatorio"));
    }
    return ResponseEntity.ok(coleccion);
  }

  public boolean eliminarColeccion(UUID id) {
    return repositorioColecciones.eliminar(id);
  }

  // Metodo auxiliar para construir criterios JSON desde parámetros
  private List<Map<String, Object>> construirCriteriosJson(
          String titulo, String descripcion, String categoria,
          String fechaReporteDesde, String fechaReporteHasta,
          String fechaAcontecimientoDesde, String fechaAcontecimientoHasta,
          String latitud, String longitud, String tipoMultimedia) {
    List<Map<String, Object>> criterios = new ArrayList<>();
    agregarCriterio(criterios, "titulo", "valor", titulo);
    agregarCriterio(criterios, "descripcion", "valor", descripcion);
    agregarCriterio(criterios, "categoria", "valor", categoria);
    if (fechaReporteDesde != null || fechaReporteHasta != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "fechareportaje");
      if (fechaReporteDesde != null) criterio.put("fechaDesde", fechaReporteDesde);
      if (fechaReporteHasta != null) criterio.put("fechaHasta", fechaReporteHasta);
      criterios.add(criterio);
    }
    if (fechaAcontecimientoDesde != null || fechaAcontecimientoHasta != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "fecha");
      if (fechaAcontecimientoDesde != null) criterio.put("fechaDesde", fechaAcontecimientoDesde);
      if (fechaAcontecimientoHasta != null) criterio.put("fechaHasta", fechaAcontecimientoHasta);
      criterios.add(criterio);
    }
    if (latitud != null && longitud != null) {
      criterios.add(Map.of(
              "tipo", "ubicacion",
              "latitud", latitud,
              "longitud", longitud
      ));
    }
    if (tipoMultimedia != null) {
      criterios.add(Map.of(
              "tipo", "multimedia",
              "tipoMultimedia", tipoMultimedia
      ));
    }
    return criterios;
  }

  private void agregarCriterio(List<Map<String, Object>> criterios, String tipo, String claveValor, String valor) {
    if (valor != null) {
      criterios.add(Map.of(
              "tipo", tipo,
              claveValor, valor
      ));
    }
  }

  @SuppressWarnings("unchecked")
  private List<Criterio> parseCriterios(Object criteriosRaw) {
    if (criteriosRaw instanceof List<?> lista) {
      return procesarCriterios((List<Map<String, Object>>) lista);
    }
    return List.of();
  }

  private List<Criterio> procesarCriterios(List<Map<String, Object>> criteriosJson) {
    return criteriosJson.stream()
            .map(this::crearCriterioDesdeJson)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  private Criterio crearCriterioDesdeJson(Map<String, Object> criterioJson) {
    String tipo = ((String) criterioJson.get("tipo")).toLowerCase();
    try {
      switch (tipo) {
        case "titulo":
          return new CriterioTitulo((String) criterioJson.get("valor"));
        case "descripcion":
          return new CriterioDescripcion((String) criterioJson.get("valor"));
        case "categoria":
          return new CriterioCategoria((String) criterioJson.get("valor"));
        case "fecha":
          LocalDate desde = parseFecha((String) criterioJson.get("fechaDesde"));
          LocalDate hasta = parseFecha((String) criterioJson.get("fechaHasta"));
          return new CriterioFecha(desde, hasta);
        case "fechareportaje":
          LocalDate desdeRpt = parseFecha((String) criterioJson.get("fechaDesde"));
          LocalDate hastaRpt = parseFecha((String) criterioJson.get("fechaHasta"));
          return new CriterioFechaReportaje(desdeRpt, hastaRpt);
        case "ubicacion":
          Float lat = parseFloat(criterioJson.get("latitud"));
          Float lon = parseFloat(criterioJson.get("longitud"));
          if (lat != null && lon != null) {
            return new CriterioUbicacion(lat, lon);
          } else {
            return null;
          }
        case "multimedia":
          String tipoMultimediaStr = (String) criterioJson.get("tipoMultimedia");
          if (tipoMultimediaStr != null) {
            TipoMultimedia tipoMultimedia = TipoMultimedia.valueOf(tipoMultimediaStr.toUpperCase());
            return new CriterioMultimedia(tipoMultimedia);
          } else {
            return null;
          }
        default:
          System.err.println("Tipo de criterio no reconocido: " + tipo);
          return null;
      }
    } catch (Exception e) {
      System.err.println("Error procesando criterio: " + e.getMessage());
      return null;
    }
  }

  private LocalDate parseFecha(String str) {
    return str != null ? LocalDate.parse(str) : null;
  }

  private Float parseFloat(Object obj) {
    return obj != null ? Float.parseFloat(obj.toString()) : null;
  }
}