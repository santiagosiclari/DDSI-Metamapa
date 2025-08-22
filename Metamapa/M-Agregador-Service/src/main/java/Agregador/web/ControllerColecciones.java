package Agregador.web;
import Agregador.Service.ServiceColecciones;
import Agregador.business.Colecciones.*;
//import DTO.ColeccionDTO;
import Agregador.business.Hechos.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-colecciones")
public class ControllerColecciones {
  private final ServiceColecciones serviceColecciones;

  public ControllerColecciones(ServiceColecciones serviceColecciones) {
    this.serviceColecciones = serviceColecciones;
  }

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
      // Construir los mapas de parámetros
      Map<String, String> paramsP = new HashMap<>();
      paramsP.put("titulo", tituloP);
      paramsP.put("descripcion", descripcionP);
      paramsP.put("categoria", categoriaP);
      paramsP.put("fecha_reporte_desde", fecha_reporte_desdeP);
      paramsP.put("fecha_reporte_hasta", fecha_reporte_hastaP);
      paramsP.put("fecha_acontecimiento_desde", fecha_acontecimiento_desdeP);
      paramsP.put("fecha_acontecimiento_hasta", fecha_acontecimiento_hastaP);
      paramsP.put("latitud", latitudP);
      paramsP.put("longitud", longitudP);
      paramsP.put("tipoMultimedia", tipoMultimediaP);
      Map<String, String> paramsNP = new HashMap<>();
      paramsNP.put("titulo", tituloNP);
      paramsNP.put("descripcion", descripcionNP);
      paramsNP.put("categoria", categoriaNP);
      paramsNP.put("fecha_reporte_desde", fecha_reporte_desdeNP);
      paramsNP.put("fecha_reporte_hasta", fecha_reporte_hastaNP);
      paramsNP.put("fecha_acontecimiento_desde", fecha_acontecimiento_desdeNP);
      paramsNP.put("fecha_acontecimiento_hasta", fecha_acontecimiento_hastaNP);
      paramsNP.put("latitud", latitudNP);
      paramsNP.put("longitud", longitudNP);
      paramsNP.put("tipoMultimedia", tipoMultimediaNP);
      ArrayList<Hecho> hechos = this.serviceColecciones.getHechosColeccion(identificador, modoNavegacion, paramsP, paramsNP);
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
      return ResponseEntity.ok(this.serviceColecciones.obtenerTodasLasColecciones());
    } catch (Exception e) {
      System.err.println("Error al obtener colecciones: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Obtener una colección por ID (get /colecciones/{id})
  @GetMapping("/{id}")
  public ResponseEntity<Coleccion> obtenerColeccionPorId(@PathVariable("id") UUID id) {
    try {
      return ResponseEntity.ok(serviceColecciones.obtenerColeccionPorId(id));
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
      return ResponseEntity.status(HttpStatus.CREATED).body(this.serviceColecciones.crearColeccion(requestBody));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno al crear la colección"));
    }
  }

  @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> actualizarColeccion(@PathVariable("id") UUID id, @RequestBody Map<String, Object> requestBody) {
    try {
      return ResponseEntity.ok(this.serviceColecciones.actualizarColeccion(id, requestBody));
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
      return ResponseEntity.ok(this.serviceColecciones.modificarAlgoritmo(id, requestBody));
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
      boolean eliminada = serviceColecciones.eliminarColeccion(id);
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
/*
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
  }*/
}