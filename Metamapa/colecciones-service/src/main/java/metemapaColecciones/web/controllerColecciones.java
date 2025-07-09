package metemapaColecciones.web;
import domain.business.criterio.*;
import domain.business.incidencias.TipoMultimedia;
import metemapaColecciones.persistencia.RepositorioColecciones;
import domain.business.Consenso.Consenso;
import domain.business.incidencias.Hecho;
import domain.business.Consenso.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;

@SpringBootApplication
@RestController
public class controllerColecciones {
  public RepositorioColecciones repositorioColecciones = new RepositorioColecciones();
  public static void main(String[] args) {
    //SpringApplication.run(testApplication.class, args);
    SpringApplication app = new SpringApplication(controllerColecciones.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "9004"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    // context.close();
  }
  /*public ArrayList<Hecho> getHechosColeccion(@PathVariable String identificador,
                                @RequestParam(value = "modoNavegacion", required = false,defaultValue = "IRRESTRICTA") String modoNavegacion,
                                @RequestParam(value = "tituloP",required = false) String tituloP,
                                @RequestParam(value = "descripcionP",required = false) String descripcionP,
                                @RequestParam(value = "categoriaP", required = false) String categoriaP,
                                @RequestParam(value = "fecha_reporte_desdeP",required = false) String fecha_reporte_desdeP,
                                @RequestParam(value = "fecha_reporte_hastaP", required = false) String fecha_reporte_hastaP,
                                @RequestParam(value = "fecha_acontecimiento_desdeP", required = false) String fecha_acontecimiento_desdeP,
                                @RequestParam(value = "fecha_acontecimiento_hastaP", required = false) String fecha_acontecimiento_hastaP,
                                @RequestParam(value = "latitudP", required = false) String latitudP,
                                @RequestParam(value = "longitudP", required = false) String longitudP,
                                @RequestParam(value = "tipoMultimediaP",required = false) String tipoMultimediaP,
                                @RequestParam(value = "tituloNP",required = false) String tituloNP,
                                @RequestParam(value = "descripcionNP",required = false) String descripcionNP,
                                @RequestParam(value = "categoriaNP", required = false) String categoriaNP,
                                @RequestParam(value = "fecha_reporte_desdeNP",required = false) String fecha_reporte_desdeNP,
                                @RequestParam(value = "fecha_reporte_hastaNP", required = false) String fecha_reporte_hastaNP,
                                @RequestParam(value = "fecha_acontecimiento_desdeNP", required = false) String fecha_acontecimiento_desdeNP,
                                @RequestParam(value = "fecha_acontecimiento_hastaNP", required = false) String fecha_acontecimiento_hastaNP,
                                @RequestParam(value = "latitudNP", required = false) String latitudNP,
                                @RequestParam(value = "longitudNP", required = false) String longitudNP,
                                @RequestParam(value = "tipoMultimediaNP",required = false) String tipoMultimediaNP)
  {
    ArrayList<Criterio> criteriosP = new ArrayList<Criterio>();
    ArrayList<Criterio> criteriosNP = new ArrayList<Criterio>();
    if (ModosDeNavegacion.valueOf(modoNavegacion) == ModosDeNavegacion.IRRESTRICTA)


    if (modoNavegacion != null)
    if(tituloP != null)criteriosP.add(new CriterioTitulo(tituloP));
    if(descripcionP != null)criteriosNP.add(new CriterioDescripcion(descripcionP));
    if(categoriaP != null)criteriosP.add(new CriterioCategoria(categoriaP));

    if(fecha_acontecimiento_desdeP != null)criteriosP.add(new CriterioFecha(LocalDate.parse(fecha_acontecimiento_desdeP),null));
    if(fecha_acontecimiento_hastaP != null)criteriosP.add(new CriterioFecha(null,LocalDate.parse(fecha_acontecimiento_hastaP)));
    if(fecha_reporte_desdeP != null)criteriosP.add(new CriterioFecha(LocalDate.parse(fecha_reporte_desdeP),null));
    if(fecha_reporte_hastaP != null)criteriosP.add(new CriterioFecha(null,LocalDate.parse(fecha_reporte_hastaP)));
    if(latitudP != null && longitudP != null)criteriosP.add(new CriterioUbicacion(Float.parseFloat(latitudP),Float.parseFloat(longitudP)));
//    if(tipoMultimediaP != null)criteriosP.add(CriterioMultimedia())

    //TODO agregar criterios de no pertenencia a la API

    //TODO query a las colecciones cuando haya persistencia
    Coleccion coleccion = new Coleccion("prueba","dummy",null,null); // = query_colecciones(identificador)
    return coleccion.filtrarPorCriterios(criteriosP,criteriosNP,ModosDeNavegacion.valueOf(modoNavegacion));

  }*/
  @GetMapping("/colecciones/{identificador}/hechos")
  @ResponseBody
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
      Optional<Coleccion> coleccionOpt = repositorioColecciones.findById(identificador);
      if (coleccionOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
      }
      Coleccion coleccion = coleccionOpt.get();
      // Validar modo de navegación
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
      ArrayList<Criterio> criteriosP = procesarCriterios(criteriosPertenenciaJson);
      // Construir criterios adicionales de NO pertenencia
      List<Map<String, Object>> criteriosNoPertenenciaJson = construirCriteriosJson(
              tituloNP, descripcionNP, categoriaNP,
              fecha_reporte_desdeNP, fecha_reporte_hastaNP,
              fecha_acontecimiento_desdeNP, fecha_acontecimiento_hastaNP,
              latitudNP, longitudNP, tipoMultimediaNP);
      ArrayList<Criterio> criteriosNP = procesarCriterios(criteriosNoPertenenciaJson);
      // Filtrar hechos usando la colección
      ArrayList<Hecho> hechos = coleccion.filtrarPorCriterios(criteriosP, criteriosNP, modo);
      return ResponseEntity.ok(hechos);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
    } catch (Exception e) {
      System.err.println("Error al obtener hechos de colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
    }
  }

  // Obtener todas las colecciones (get /colecciones)
  @GetMapping("/colecciones")
  @ResponseBody
  public ResponseEntity<List<ColeccionDTO>> obtenerTodasLasColecciones() {
    try {
      List<ColeccionDTO> coleccionesDTO = repositorioColecciones.obtenerTodas().stream()
              .map(ColeccionDTO::new)
              .collect(Collectors.toList());
      return ResponseEntity.ok(coleccionesDTO);
    } catch (Exception e) {
      System.err.println("Error al obtener colecciones: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Obtener una colección por ID (get /colecciones/{id})
  @GetMapping("/colecciones/{id}")
  @ResponseBody
  public ResponseEntity<ColeccionDTO> obtenerColeccionPorId(@PathVariable("id") UUID id) {
    try {
      Optional<Coleccion> coleccionOpt = repositorioColecciones.findById(id);
      if (coleccionOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      return ResponseEntity.ok(new ColeccionDTO(coleccionOpt.get()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      System.err.println("Error al obtener colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Crear una coleccion (post /colecciones)
  @PostMapping(value = "/colecciones", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity<ColeccionDTO> crearColeccion(@RequestBody Map<String, Object> requestBody) {
    try {
      String titulo = (String) requestBody.get("titulo");
      String descripcion = (String) requestBody.get("descripcion");
      // Validar que el título no sea null ni vacío
      if (titulo == null || titulo.trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }
      // Procesar criterios de pertenencia
      ArrayList<Criterio> criteriosPertenencia = new ArrayList<>();
      if (requestBody.containsKey("criteriosPertenencia")) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> criteriosP = (List<Map<String, Object>>) requestBody.get("criteriosPertenencia");
        criteriosPertenencia = procesarCriterios(criteriosP);
      }
      // Procesar criterios de no pertenencia
      ArrayList<Criterio> criteriosNoPertenencia = new ArrayList<>();
      if (requestBody.containsKey("criteriosNoPertenencia")) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> criteriosNP = (List<Map<String, Object>>) requestBody.get("criteriosNoPertenencia");
        criteriosNoPertenencia = procesarCriterios(criteriosNP);
      }
      Coleccion coleccion = new Coleccion(titulo, descripcion, criteriosPertenencia, criteriosNoPertenencia);
      if (requestBody.containsKey("consenso")) {
        String consensoStr = (String) requestBody.get("consenso");
        Consenso consenso = Consenso.stringToConsenso(consensoStr);
        coleccion.setConsenso(consenso);
      }
      System.out.println("Colección creada: " + coleccion);
      repositorioColecciones.save(coleccion);
      return ResponseEntity.status(HttpStatus.CREATED).body(new ColeccionDTO(coleccion));
    } catch (Exception e) {
      System.err.println("Error al crear colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Actualizar una colección completa (put /colecciones/{id})
  @PutMapping(value = "/colecciones/{id}", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity<ColeccionDTO> actualizarColeccion(@PathVariable("id") UUID id, @RequestBody Map<String, Object> requestBody) {
    try {
      // Buscar la colección existente
      Optional<Coleccion> coleccionOpt = repositorioColecciones.findById(id);
      if (coleccionOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      Coleccion coleccion = coleccionOpt.get();
      // Actualizar título si está presente
      if (requestBody.containsKey("titulo")) {
        coleccion.setTitulo((String) requestBody.get("titulo"));
      }
      // Actualizar descripción si está presente
      if (requestBody.containsKey("descripcion")) {
        coleccion.setDescripcion((String) requestBody.get("descripcion"));
      }
      // Actualizar consenso si está presente
      if (requestBody.containsKey("consenso")) {
        String consensoStr = (String) requestBody.get("consenso");
        Consenso nuevoConsenso = Consenso.stringToConsenso(consensoStr);
        coleccion.setConsenso(nuevoConsenso);
      }
      // Actualizar criterios de pertenencia si están presentes
      if (requestBody.containsKey("criteriosPertenencia")) {
        List<Map<String, Object>> criteriosData = (List<Map<String, Object>>) requestBody.get("criteriosPertenencia");
        ArrayList<Criterio> nuevosCriterios = new ArrayList<>();

        for (Map<String, Object> criterioData : criteriosData) {
          Criterio criterio = crearCriterioDesdeMap(criterioData);
          if (criterio != null) {
            nuevosCriterios.add(criterio);
          }
        }
        coleccion.setCriterioPertenencia(nuevosCriterios);
      }
      // Actualizar criterios de no pertenencia si están presentes
      if (requestBody.containsKey("criteriosNoPertenencia")) {
        List<Map<String, Object>> criteriosData = (List<Map<String, Object>>) requestBody.get("criteriosNoPertenencia");
        ArrayList<Criterio> nuevosCriterios = new ArrayList<>();

        for (Map<String, Object> criterioData : criteriosData) {
          Criterio criterio = crearCriterioDesdeMap(criterioData);
          if (criterio != null) {
            nuevosCriterios.add(criterio);
          }
        }
        coleccion.setCriterioNoPertenencia(nuevosCriterios);
      }
      // Guardar los cambios
      repositorioColecciones.update(coleccion);
      // Retornar la colección actualizada como DTO
      return ResponseEntity.ok(new ColeccionDTO(coleccion));
    } catch (IllegalArgumentException e) {
      // UUID inválido o consenso inválido
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      System.err.println("Error al actualizar colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Modificar algoritmo de consenso (patch /colecciones/{id})
  @PatchMapping(value = "/colecciones/{id}", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity<ColeccionDTO> modificarAlgoritmo(@PathVariable("id") UUID id, @RequestBody Map<String, Object> requestBody) {
    try {
      Optional<Coleccion> coleccionOpt = repositorioColecciones.findById(id);
      if (coleccionOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      Coleccion coleccion = coleccionOpt.get();
      if (!requestBody.containsKey("Consenso")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }
      Consenso nuevoConsenso = Consenso.stringToConsenso((String) requestBody.get("Consenso"));
      coleccion.setConsenso(nuevoConsenso);
      repositorioColecciones.update(coleccion);
      return ResponseEntity.ok(new ColeccionDTO(coleccion));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Eliminar una colección (delete /colecciones/{id})
  @DeleteMapping("/colecciones/{id}")
  @ResponseBody
  public ResponseEntity<Void> eliminarColeccion(@PathVariable("id") UUID id) {
    try {
      Optional<Coleccion> coleccionOpt = repositorioColecciones.findById(id);
      if (coleccionOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
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
    // Criterio de título
    if (titulo != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "titulo");
      criterio.put("valor", titulo);
      criterios.add(criterio);
    }
    // Criterio de descripción
    if (descripcion != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "descripcion");
      criterio.put("valor", descripcion);
      criterios.add(criterio);
    }
    // Criterio de categoría
    if (categoria != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "categoria");
      criterio.put("valor", categoria);
      criterios.add(criterio);
    }
    // Criterio de fecha de reporte
    if (fechaReporteDesde != null || fechaReporteHasta != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "fechareportaje");
      if (fechaReporteDesde != null) criterio.put("fechaDesde", fechaReporteDesde);
      if (fechaReporteHasta != null) criterio.put("fechaHasta", fechaReporteHasta);
      criterios.add(criterio);
    }
    // Criterio de fecha de acontecimiento
    if (fechaAcontecimientoDesde != null || fechaAcontecimientoHasta != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "fecha");
      if (fechaAcontecimientoDesde != null) criterio.put("fechaDesde", fechaAcontecimientoDesde);
      if (fechaAcontecimientoHasta != null) criterio.put("fechaHasta", fechaAcontecimientoHasta);
      criterios.add(criterio);
    }
    // Criterio de ubicación
    if (latitud != null && longitud != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "ubicacion");
      criterio.put("latitud", latitud);
      criterio.put("longitud", longitud);
      criterios.add(criterio);
    }
    // Criterio de multimedia
    if (tipoMultimedia != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "multimedia");
      criterio.put("tipoMultimedia", tipoMultimedia);
      criterios.add(criterio);
    }
    return criterios;
  }

  // Metodo auxiliar para crear criterios desde Map
  private Criterio crearCriterioDesdeMap(Map<String, Object> criterioData) {
    try {
      String tipo = (String) criterioData.get("tipo");
      Map<String, Object> parametros = (Map<String, Object>) criterioData.get("parametros");
      return switch (tipo) {
        case "CriterioCategoria" -> new CriterioCategoria((String) parametros.get("categoria"));
        case "CriterioTitulo" -> new CriterioTitulo((String) parametros.get("titulo"));
        case "CriterioDescripcion" -> new CriterioDescripcion((String) parametros.get("descripcion"));
        case "CriterioFecha" -> new CriterioFecha(
                LocalDate.parse((String) parametros.get("fechaDesde")),
                LocalDate.parse((String) parametros.get("fechaHasta"))
        );
        case "CriterioFechaReportaje" -> new CriterioFechaReportaje(
                LocalDate.parse((String) parametros.get("fechaDesde")),
                LocalDate.parse((String) parametros.get("fechaHasta"))
        );
        case "CriterioUbicacion" -> new CriterioUbicacion(((Number)  parametros.get("latitud")).floatValue(), ((Number) parametros.get("longitud")).floatValue());
        case "CriterioMultimedia" -> new CriterioMultimedia(TipoMultimedia.valueOf(((String) parametros.get("tipoMultimedia")).toUpperCase()));
        default -> {
          System.err.println("Tipo de criterio no reconocido: " + tipo);
          yield null;
        }
      };
    } catch (Exception e) {
      System.err.println("Error al crear criterio: " + e.getMessage());
      return null;
    }
  }

  // Metodo auxiliar para procesar criterios
  private ArrayList<Criterio> procesarCriterios(List<Map<String, Object>> criteriosJson) {
    ArrayList<Criterio> criterios = new ArrayList<>();
    for (Map<String, Object> criterioJson : criteriosJson) {
      String tipo = (String) criterioJson.get("tipo");
      switch (tipo.toLowerCase()) {
        case "titulo":
          String titulo = (String) criterioJson.get("valor");
          if (titulo != null) {
            criterios.add(new CriterioTitulo(titulo));
          }
          break;
        case "descripcion":
          String descripcion = (String) criterioJson.get("valor");
          if (descripcion != null) {
            criterios.add(new CriterioDescripcion(descripcion));
          }
          break;
        case "categoria":
          String categoria = (String) criterioJson.get("valor");
          if (categoria != null) {
            criterios.add(new CriterioCategoria(categoria));
          }
          break;
        case "fecha":
          String fechaDesdeStr = (String) criterioJson.get("fechaDesde");
          String fechaHastaStr = (String) criterioJson.get("fechaHasta");
          LocalDate fechaDesde = fechaDesdeStr != null ? LocalDate.parse(fechaDesdeStr) : null;
          LocalDate fechaHasta = fechaHastaStr != null ? LocalDate.parse(fechaHastaStr) : null;
          criterios.add(new CriterioFecha(fechaDesde, fechaHasta));
          break;
        case "fechareportaje":
          String reportajeDesdeStr = (String) criterioJson.get("fechaDesde");
          String reportajeHastaStr = (String) criterioJson.get("fechaHasta");
          LocalDate reportajeDesde = reportajeDesdeStr != null ? LocalDate.parse(reportajeDesdeStr) : null;
          LocalDate reportajeHasta = reportajeHastaStr != null ? LocalDate.parse(reportajeHastaStr) : null;
          criterios.add(new CriterioFechaReportaje(reportajeDesde, reportajeHasta));
          break;
        case "ubicacion":
          Object latitudObj = criterioJson.get("latitud");
          Object longitudObj = criterioJson.get("longitud");
          if (latitudObj != null && longitudObj != null) {
            Float latitud = Float.parseFloat(latitudObj.toString());
            Float longitud = Float.parseFloat(longitudObj.toString());
            criterios.add(new CriterioUbicacion(latitud, longitud));
          }
          break;
        case "multimedia":
          String tipoMultimediaStr = (String) criterioJson.get("tipoMultimedia");
          if (tipoMultimediaStr != null) {
            TipoMultimedia tipoMultimedia = TipoMultimedia.valueOf(tipoMultimediaStr.toUpperCase());
            criterios.add(new CriterioMultimedia(tipoMultimedia));
          }
          break;
        default:
          System.err.println("Tipo de criterio no reconocido: " + tipo);
          break;
      }
    }
    return criterios;
  }

  public static class ColeccionDTO {
    @Getter
    private String titulo;
    @Getter
    private String descripcion;
    @Getter
    private UUID handle;
    @Getter
    private String consenso;
    @Getter
    private List<CriterioDTO> criteriosPertenencia;
    @Getter
    private List<CriterioDTO> criteriosNoPertenencia;

    public ColeccionDTO(Coleccion coleccion) {
      this.titulo = coleccion.getTitulo();
      this.descripcion = coleccion.getDescripcion();
      this.handle = coleccion.getHandle();
      this.consenso = coleccion.getConsenso() != null ? coleccion.getConsenso().getClass().getSimpleName() : null;
      this.criteriosPertenencia = Optional.ofNullable(coleccion.getCriterioPertenencia())
              .orElse(new ArrayList<>()).stream()
              .map(CriterioDTO::new)
              .collect(Collectors.toList());
      this.criteriosNoPertenencia = Optional.ofNullable(coleccion.getCriterioNoPertenencia())
              .orElse(new ArrayList<>()).stream()
              .map(CriterioDTO::new)
              .collect(Collectors.toList());
    }
  }

  public static class CriterioDTO {
    @Getter
    private String tipo;
    @Getter
    private Map<String, Object> parametros;

    public CriterioDTO(Criterio criterio) {
      this.tipo = criterio.getClass().getSimpleName();
      this.parametros = new HashMap<>();
      // Extraer parámetros específicos según el tipo de criterio
      if (criterio instanceof CriterioTitulo ct) {
        parametros.put("titulo", ct.getTitulo());
      } else if (criterio instanceof CriterioDescripcion cd) {
        parametros.put("descripcion", cd.getDescripcion());
      } else if (criterio instanceof CriterioCategoria cc) {
        parametros.put("categoria", cc.getCategoria());
      } else if (criterio instanceof CriterioFecha cf) {
        parametros.put("fechaDesde", cf.getFechaDesde());
        parametros.put("fechaHasta", cf.getFechaHasta());
      } else if (criterio instanceof CriterioFechaReportaje cfr) {
        parametros.put("fechaDesde", cfr.getDesde());
        parametros.put("fechaHasta", cfr.getHasta());
      } else if (criterio instanceof CriterioUbicacion cu) {
        parametros.put("latitud", cu.getLatitud());
        parametros.put("longitud", cu.getLongitud());
      } else if (criterio instanceof CriterioMultimedia cm) {
        parametros.put("tipoMultimedia", cm.getTipoMultimedia().name());
      }
    }
  }
}