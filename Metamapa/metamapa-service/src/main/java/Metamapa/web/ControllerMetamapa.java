package Metamapa.web;
import Metamapa.DTO.AccionSolicitudDTO;
import Metamapa.DTO.HechoDTO;
import Metamapa.DTO.SolicitudEliminacionDTO;
import Metamapa.business.Colecciones.Coleccion;
import Metamapa.business.Consenso.ModosDeNavegacion;
import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.Hechos.*;
import Metamapa.service.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/metamapa")
public class ControllerMetamapa {
  private final ServiceFuenteDeDatos serviceFuenteDeDatos;
  private final ServiceAgregador serviceAgregador;
  //private final ServiceIncidencias serviceIncidencias;
  private final ServiceColecciones serviceColecciones;

  public ControllerMetamapa(ServiceFuenteDeDatos serviceFuenteDeDatos,
                            ServiceAgregador serviceAgregador,
                            ServiceIncidencias serviceIncidencias,
                            ServiceColecciones serviceColecciones) {
    this.serviceFuenteDeDatos = serviceFuenteDeDatos;
    this.serviceAgregador = serviceAgregador;
    //this.serviceIncidencias = serviceIncidencias;
    this.serviceColecciones = serviceColecciones;
  }

  // API Administrativa de MetaMapa
  //● Operaciones CRUD sobre las colecciones.
  @GetMapping(value= {"/colecciones/", "/colecciones"}, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public List<Coleccion> obtenerColecciones() {
    return serviceColecciones.getColecciones();
  }

  @GetMapping(value="/colecciones/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Coleccion> obtenerColeccion(@PathVariable UUID uuid) {
    var c = serviceColecciones.getColeccion(uuid);
    return (c != null) ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
  }

  @PostMapping(value="/colecciones", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<?> crearColeccion(@RequestParam String titulo, @RequestParam String descripcion, @RequestParam String consenso,
                               @RequestParam(required = false) List<String> pertenenciaTitulos,
                               @RequestParam(required = false) List<String> noPertenenciaTitulos,
                               RedirectAttributes ra) {
    // default del consenso si no viene
    String consensoEfectivo = (consenso == null || consenso.isBlank())
            ? "MayoriaSimple" : consenso;

    List<Map<String, Object>> pertenencia = new ArrayList<>();
    if (pertenenciaTitulos != null) {
      for (String t : pertenenciaTitulos) {
        if (!t.isBlank())
          pertenencia.add(Map.of("tipo", "titulo", "valor", t));
      }
    }
    List<Map<String, Object>> noPertenencia = new ArrayList<>();
    if (noPertenenciaTitulos != null) {
      for (String t : noPertenenciaTitulos) {
        if (!t.isBlank())
          noPertenencia.add(Map.of("tipo", "titulo", "valor", t));
      }
    }
    UUID id = serviceColecciones.crearColeccion(
            titulo.trim(),
            descripcion.trim(),
            consensoEfectivo,
            pertenencia,
            noPertenencia
    );

    return ResponseEntity
            .created(URI.create("/metamapa/colecciones/" + id))
            .body(Map.of("id", id.toString()));
  }

  @DeleteMapping(value="/colecciones/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Map<String, String>> eliminarColeccion(@PathVariable UUID uuid) {
    HttpStatus status = serviceColecciones.deleteColeccion(uuid);

    if (status == HttpStatus.NO_CONTENT) {
      return ResponseEntity.ok(Map.of("mensaje", "Colección eliminada correctamente"));
    } else if (status == HttpStatus.NOT_FOUND) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(Map.of("error", "Colección no encontrada"));
    } else {
      return ResponseEntity.status(status)
              .body(Map.of("error", "No se pudo eliminar la colección"));
    }
  }
  //● Modificación del algoritmo de consenso.
  @PatchMapping(value="/colecciones/{uuid}",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Void> modificarConsenso(
          @PathVariable UUID uuid,
          @RequestBody Map<String,String> body) {
    String consenso = body.get("consenso");
    serviceColecciones.actualizarAlgoritmoConsenso(uuid, consenso);
    return ResponseEntity.noContent().build();
  }

  //● Agregar fuentes de hechos de una colección.
  @PostMapping("/colecciones/{uuid}/fuente/{idFuente}")
  @ResponseBody
  public ResponseEntity<?> agregarFuente(@PathVariable UUID uuid, @PathVariable Integer idFuente) {
    try {
      Coleccion col = serviceColecciones.getColeccion(uuid);
      if (col == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Colección no encontrada");
      serviceAgregador.agregarFuenteAColeccion(uuid, idFuente);
      // ⚠️ Quitá esta línea si no existe ese endpoint en el Agregador:
      // serviceAgregador.actualizarAgregador();
      return ResponseEntity.noContent().build();
    } catch (org.springframework.web.client.HttpStatusCodeException ex) {
      return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
    } catch (org.springframework.web.client.ResourceAccessException ex) {
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("No se pudo contactar al Agregador: " + ex.getMessage());
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
  }

  //Quitar fuentes de hechos de una colección
  @DeleteMapping("/colecciones/{uuid}/fuente/{idFuente}")
  @ResponseBody
  public ResponseEntity<Void> quitarFuente(@PathVariable UUID uuid, @PathVariable Integer idFuente) {
    Coleccion coleccion = serviceColecciones.getColeccion(uuid);
    if (coleccion == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
      try {
          serviceAgregador.removerFuente(idFuente);
          serviceAgregador.actualizarAgregador();
          return ResponseEntity.noContent().build();
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
      }
    }

  // ● Aprobar o denegar una solicitud de eliminación (endpoint único)
  //TODO: CHEQUEAR
  @PatchMapping(value="/api/solicitudesEliminacion/{id}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Void> resolverSolicitud(@PathVariable Integer id,
                                                @RequestBody AccionSolicitudDTO dto) {
    String accion = dto.getAccion();
    if (accion == null) {
      return ResponseEntity.unprocessableEntity().build();
    }

    ServiceAgregador.Result r;
    try {
      if ("APROBAR".equalsIgnoreCase(accion.trim())) {
        r = serviceAgregador.aprobarSolicitudEliminacion(id);
      } else if ("RECHAZAR".equalsIgnoreCase(accion.trim())) {
        r = serviceAgregador.rechazarSolicitudEliminacion(id);
      } else {
        return ResponseEntity.unprocessableEntity().build(); // acción inválida
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return switch (r) {
      case OK        -> ResponseEntity.noContent().build();   // 204
      case NOT_FOUND -> ResponseEntity.notFound().build();    // 404
      case CONFLICT  -> ResponseEntity.status(409).build();   // ya resuelta
      default        -> ResponseEntity.unprocessableEntity().build(); // 422
    };
  }

  // API Pública para otras instancias de MetaMapa
  //● Consulta de hechos dentro de una colección.
  @GetMapping("/colecciones/{idColeccion}/hechos")
  public ArrayList<Hecho> consultarHechos(@PathVariable("idColeccion") UUID id) {
    return serviceColecciones.getHechosDeColeccion(id);
  }

  //● TODO Generar una solicitud de eliminación a un hecho.
  @PostMapping(value = "/api/solicitudesEliminacion", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Map<String,Object>> generarSolicitudEliminacion(@Valid @RequestBody SolicitudEliminacionDTO dto) {
    Integer idSolicitud = serviceAgregador.crearSolicitudEliminacionYRetornarId(
            dto.getIdHechoAfectado(),
            dto.getMotivo(),
            dto.getUrl()
    );

    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()       // /metamapa/api/solicitudesEliminacion
            .path("/{id}")              // /{id}
            .buildAndExpand(idSolicitud)
            .toUri();

    return ResponseEntity
            .created(location)
            .body(Map.of("idSolicitud", idSolicitud));
  }

  @GetMapping(value="/api/solicitudesEliminacion/{id}", produces = "application/json")
  @ResponseBody
  public ResponseEntity<Map<String,Object>> getSolicitudEliminacion(@PathVariable Integer id) {
    Map<String,Object> solicitud = serviceAgregador.obtenerSolicitudEliminacion(id);
    if (solicitud == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(solicitud);
  }


  //● TODO: Navegación filtrada sobre una colección.
  @GetMapping("/colecciones/{idColeccion}/hechos/")
  public List<Hecho> navegarFiltrado(
          @PathVariable UUID idColeccion,
          // filtros
          @RequestParam(required = false) String categoria,
          @RequestParam(required = false) String titulo,
          @RequestParam(required = false) String descripcion,
          // fechas
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_reporte_desde,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_reporte_hasta,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_acontecimiento_desde,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_acontecimiento_hasta,
          // ubicación
          @RequestParam(required = false) Float ubicacion_latitud,
          @RequestParam(required = false) Float ubicacion_longitud,
          @RequestParam(required = false) Double radio_km,
          // otros
          @RequestParam(required = false) Integer id_fuente,
          @RequestParam(required = false) TipoMultimedia tipo_multimedia,
          // modo
          @RequestParam(defaultValue = "CURADA") ModosDeNavegacion modo
  ) {
    return serviceColecciones.navegarFiltrado(
            idColeccion, modo, categoria, titulo, descripcion,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion_latitud, ubicacion_longitud, radio_km,
            id_fuente, tipo_multimedia
    );
  }

    //● TODO: Navegación curada o irrestricta sobre una colección.
  @GetMapping("/colecciones/{idColeccion}/hechos/{modo}")
  public ArrayList<Hecho> obtenerHechosNavegacion(@PathVariable("idColeccion") UUID id,
                                                  @PathVariable("modo") ModosDeNavegacion modosDeNavegacion) {
      Coleccion coleccion = serviceColecciones.getColeccion(id);
      return coleccion.getHechos(coleccion.getAgregador().getListaDeHechos(), modosDeNavegacion);
  }

  //● TODO: Reportar un hecho. Supongo que se refiere a crear una solicitud de edicion
  @PostMapping("/solicitudesEdicion/")
  public ResponseEntity<Map<String,Object>> generarSolicitudEdicion(@RequestParam("hechoAfectado") String hechoAfectado,
                                                                   @RequestParam("motivo") String motivo,
                                                                   @RequestParam(value = "url", required = false) String url) {
    Integer idSolicitud = serviceAgregador.crearSolicitudEdicionYRetornarId(hechoAfectado, motivo, url);
    return ResponseEntity
            .created(URI.create("/metamapa/solicitudesEdicion/" + idSolicitud))
            .body(Map.of("idSolicitud", idSolicitud));
  }


  // UNO (passthrough de JSON)
  @GetMapping(value = "/api/fuentesDeDatos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> obtenerFuente(@PathVariable Integer id) {
    try {
      String json = serviceFuenteDeDatos.getFuenteDeDatosRaw(id);
      return ResponseEntity.ok(json);
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"" + e.getMessage() + "\"}");
    } catch (RestClientException e) {
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("{\"error\":\"Upstream error: " + e.getMessage() + "\"}");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
    }
  }

  // LISTA (passthrough de JSON)
  @GetMapping(value = "/api/fuentesDeDatos", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> listarFuentes() {
    try {
      String json = serviceFuenteDeDatos.getFuentesDeDatosRaw();
      return ResponseEntity.ok(json);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
    }
  }



  /*
    @PostMapping(value = "/metamapa/fuentesDeDatos/", consumes = "application/json", produces = "application/json")
    public  ResponseEntity<String> crearFuenteDeDatos(@RequestBody String requestBody) {
      ResponseEntity<String> json = serviceFuenteDeDatos.crearFuente(requestBody);
      return ResponseEntity.status(json.getStatusCode()).headers(json.getHeaders()).body(json.getBody());

    }
  */
  @PostMapping(value = "/api/fuentesDeDatos/", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Map<String,Object>> crearFuenteDeDatos(@RequestBody Map<String, String> payload) {
    String tipo = payload.get("tipo");
    String nombre = payload.get("nombre");
    String url = payload.get("url");

    Integer idFuente = serviceFuenteDeDatos.crearFuenteYRetornarId(tipo, nombre, url);

    Map<String,Object> body = new HashMap<>();
    body.put("id", idFuente);
    body.put("nombre", nombre);
    body.put("tipo", tipo);

    return ResponseEntity
            .created(URI.create("/metamapa/fuentesDeDatos/" + idFuente))
            .body(body);
  }

  //TODO No necesitamos conectarnos con el agregador
  @GetMapping("/agregador/hechos")
  public String obtenerHechosAgregador(Model model) {
    model.addAttribute("hechos", serviceAgregador.getAgregadorHechos());
    return "agregador";
  }

  @GetMapping("/agregador/")
  public String obtenerAgregador(Model model) {
    model.addAttribute("agregador", serviceAgregador.getAgregador());
    return "agregador";
  }

  @PostMapping("/agregador/fuentes/actualizar")
  public ResponseEntity<Void> actualizarAgregador() {
    serviceAgregador.actualizarAgregador();
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/agregador/fuentesDeDatos/agregar/{idFuenteDeDatos}")
  public ResponseEntity<Void> agregarFuente(@PathVariable("idFuenteDeDatos") Integer idFuente) {
    serviceAgregador.agregarFuente(idFuente);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/agregador/fuentesDeDatos/remover/{idFuenteDeDatos}")
  public ResponseEntity<Void> removerFuente(@PathVariable("idFuenteDeDatos") Integer idFuente) {
    serviceAgregador.removerFuente(idFuente);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/fuentesDeDatos/{idFuenteDeDatos}/csv")
  public String cargarCSV(@PathVariable("idFuenteDeDatos") Integer idFuenteDeDatos,
                          @RequestParam("file") MultipartFile file, RedirectAttributes ra) throws IOException {
    serviceFuenteDeDatos.cargarCSV(idFuenteDeDatos, file);
    ra.addFlashAttribute("success", "CSV cargado correctamente");
    return "redirect:/metamapa/fuentesDeDatos/" + idFuenteDeDatos;
  }

  @PostMapping(
          value = "/fuentesDeDatos/{idFuenteDeDatos}/hechos",
          consumes = "application/json",
          produces = "application/json")
  public ResponseEntity<?> cargarHecho(@PathVariable Integer idFuenteDeDatos, @Valid @RequestBody HechoDTO dto
  ) {
    dto.validarCoordenadas();

    // Si es anónimo, ignorá autor
    String autor = (dto.getAnonimo() != null && dto.getAnonimo()) ? null : dto.getAutor();

    // Convertir multimedia a dominio
    var multimediaDomain = dto.toMultimediaDomain();

    // Llamar a tu service (como ya lo tenés implementado)
    Integer idGenerado = serviceFuenteDeDatos.cargarHecho(
            idFuenteDeDatos,
            dto.getTitulo().trim(),
            blankToNull(dto.getDescripcion()),
            blankToNull(dto.getCategoria()),
            dto.getLatitud(),
            dto.getLongitud(),
            dto.getFechaHecho(),
            blankToNull(autor),
            dto.getAnonimo() != null && dto.getAnonimo(),
            multimediaDomain
    );

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(java.util.Map.of("id", idGenerado));
  }

  private static String blankToNull(String s) {
    return (s == null || s.isBlank()) ? null : s.trim();
  }

  @GetMapping("/")
  public String mostrarHome(Model model) {
    return "home";
  }

}
