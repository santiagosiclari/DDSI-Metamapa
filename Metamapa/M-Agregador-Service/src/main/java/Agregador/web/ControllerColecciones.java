package Agregador.web;
import Agregador.business.Colecciones.*;
//import DTO.ColeccionDTO;
import Agregador.persistencia.RepositorioColecciones;
import Agregador.business.Hechos.*;
import Agregador.business.Consenso.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-colecciones")
public class ControllerColecciones {
  public RepositorioColecciones repositorioColecciones = new RepositorioColecciones();

  @GetMapping("/{identificador}")
  public ResponseEntity<ArrayList<Hecho>> getHechosColeccion(
          @PathVariable("identificador") UUID identificador,
          @RequestParam(value = "modoNavegacion", required = false, defaultValue = "IRRESTRICTA") String modoNavegacion,
          @RequestParam(value = "tituloP", required = false) String tituloP,
          @RequestParam(value = "descripcionP", required = false) String descripcionP,
          @RequestParam(value = "categoriaP", required = false) String categoriaP,
          @RequestParam(value = "fecha_reporte_desdeP", required = false) String fecha_reporte_desdeP,
          @RequestParam(value = "fecha_reporte_hastaP", required = false) String fecha_reporte_hastaP,
          @RequestParam(value = "fecha_acontecimiento_desdeP", required = false) String fecha_acontecimiento_desdeP,
          @RequestParam(value = "fecha_acontecimiento_hastaP", required = false) String fecha_acontecimiento_hastaP,
          @RequestParam(value = "latitudP", required = false) String latitudP,
          @RequestParam(value = "longitudP", required = false) String longitudP,
          @RequestParam(value = "tipoMultimediaP", required = false) String tipoMultimediaP,
          @RequestParam(value = "tituloNP", required = false) String tituloNP,
          @RequestParam(value = "descripcionNP", required = false) String descripcionNP,
          @RequestParam(value = "categoriaNP", required = false) String categoriaNP,
          @RequestParam(value = "fecha_reporte_desdeNP", required = false) String fecha_reporte_desdeNP,
          @RequestParam(value = "fecha_reporte_hastaNP", required = false) String fecha_reporte_hastaNP,
          @RequestParam(value = "fecha_acontecimiento_desdeNP", required = false) String fecha_acontecimiento_desdeNP,
          @RequestParam(value = "fecha_acontecimiento_hastaNP", required = false) String fecha_acontecimiento_hastaNP,
          @RequestParam(value = "latitudNP", required = false) String latitudNP,
          @RequestParam(value = "longitudNP", required = false) String longitudNP,
          @RequestParam(value = "tipoMultimediaNP", required = false) String tipoMultimediaNP) {
    try {
      Coleccion coleccion = repositorioColecciones.buscarXUUID(identificador);
      /*if (coleccionOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
      }
      Coleccion coleccion = coleccionOpt.get();
      // Validar modo de navegación
      */
      ModosDeNavegacion modo;
      try {
        modo = ModosDeNavegacion.valueOf(modoNavegacion.toUpperCase());
      } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
      }
      // Construir criterios adicionales de pertenencia
      List<Map<String, Object>> criteriosPertenenciaJson = construirCriteriosJson(
              tituloP, descripcionP, categoriaP,
              fecha_reporte_desdeP, fecha_reporte_hastaP,
              fecha_acontecimiento_desdeP, fecha_acontecimiento_hastaP,
              latitudP, longitudP, tipoMultimediaP);
      ArrayList<Criterio> criteriosP = (ArrayList<Criterio>) procesarCriterios(criteriosPertenenciaJson);
      // Construir criterios adicionales de NO pertenencia
      List<Map<String, Object>> criteriosNoPertenenciaJson = construirCriteriosJson(
              tituloNP, descripcionNP, categoriaNP,
              fecha_reporte_desdeNP, fecha_reporte_hastaNP,
              fecha_acontecimiento_desdeNP, fecha_acontecimiento_hastaNP,
              latitudNP, longitudNP, tipoMultimediaNP);
      ArrayList<Criterio> criteriosNP = (ArrayList<Criterio>) procesarCriterios(criteriosNoPertenenciaJson);
      // Filtrar hechos usando la colección
      //TODO
      //ArrayList<Hecho> hechos = coleccion.filtrarPorCriterios(serviceAgregador.getAgregadorHechos(),criteriosP, criteriosNP, modo);

      return ResponseEntity.ok(hechos);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
    } catch (Exception e) {
      System.err.println("Error al obtener hechos de colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
    }
  }

  // Obtener todas las colecciones (get /colecciones)
  @GetMapping("/")
  public ResponseEntity<ArrayList<Coleccion>> obtenerTodasLasColecciones() {
    try {
      ArrayList<Coleccion> colecciones = repositorioColecciones.getColecciones();
      return ResponseEntity.ok(colecciones);
    } catch (Exception e) {
      System.err.println("Error al obtener colecciones: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Obtener una colección por ID (get /colecciones/{id})
  @GetMapping("/{id}")
  public ResponseEntity<Coleccion> obtenerColeccionPorId(@PathVariable("id") UUID id) {
    try {
      Coleccion coleccion = repositorioColecciones.buscarXUUID(id);
      return ResponseEntity.ok(coleccion);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      System.err.println("Error al obtener colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Crear una coleccion (post /colecciones)
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> crearColeccion(@RequestBody Map<String, Object> requestBody) {
    try {
      String titulo = (String) requestBody.get("titulo");
      if (titulo == null || titulo.isBlank()) {
        return ResponseEntity.badRequest().body(Map.of("error", "El campo 'titulo' es obligatorio"));
      }
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
      return ResponseEntity.status(HttpStatus.CREATED).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno al crear la colección"));
    }
  }

  @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> actualizarColeccion(@PathVariable("id") UUID id, @RequestBody Map<String, Object> requestBody) {
    try {
      Coleccion coleccion = repositorioColecciones.buscarXUUID(id);
      /*if (coleccionOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      Coleccion coleccion = coleccionOpt.get();
      */
      if (requestBody.containsKey("titulo")) {
        coleccion.setTitulo((String) requestBody.get("titulo"));
      }
      if (requestBody.containsKey("descripcion")) {
        coleccion.setDescripcion((String) requestBody.get("descripcion"));
      }
      if (requestBody.containsKey("consenso")) {
        String consensoStr = (String) requestBody.get("consenso");
        Consenso nuevoConsenso = Consenso.stringToConsenso(consensoStr);
        coleccion.setConsenso(nuevoConsenso);
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
      return ResponseEntity.ok(null);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      System.err.println("Error al actualizar colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Modificar algoritmo de consenso (patch /colecciones/{id})
  @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> modificarAlgoritmo(@PathVariable("id") UUID id, @RequestBody Map<String, Object> requestBody) {
    try {
      Coleccion coleccion = repositorioColecciones.buscarXUUID(id);
      if (!requestBody.containsKey("Consenso")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }
      Consenso nuevoConsenso = Consenso.stringToConsenso((String) requestBody.get("Consenso"));
      coleccion.setConsenso(nuevoConsenso);
      return ResponseEntity.ok(null);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Eliminar una colección (delete /colecciones/{id})
  @DeleteMapping("/colecciones/{id}")
  public ResponseEntity<Void> eliminarColeccion(@PathVariable("id") UUID id) {
    try {
      Coleccion coleccion = repositorioColecciones.buscarXUUID(id);
      boolean eliminada = repositorioColecciones.eliminar(id);
      if (eliminada) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (Exception e) {
      System.err.println("Error al eliminar colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
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