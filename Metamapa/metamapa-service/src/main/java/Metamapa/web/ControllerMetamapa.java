package Metamapa.web;
import Metamapa.business.Colecciones.Coleccion;
import Metamapa.business.Consenso.ModosDeNavegacion;
import Metamapa.business.Hechos.*;
import Metamapa.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@RestController
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
  @GetMapping(value="/metamapa/colecciones/")
  public List<Coleccion> obtenerColecciones() {
    return serviceColecciones.getColecciones();
  }

  @GetMapping(value="/metamapa/colecciones/{uuid}")
  public ResponseEntity<Coleccion> obtenerColeccion(@PathVariable UUID uuid) {
    Coleccion coleccion = serviceColecciones.getColeccion(uuid);
    return (coleccion != null) ? ResponseEntity.ok(coleccion) : ResponseEntity.notFound().build();
  }

  @PostMapping("/metamapa/colecciones/")
  public ResponseEntity crearColeccion(@RequestParam String titulo, @RequestParam String descripcion, @RequestParam String consenso,
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

  @DeleteMapping("/metamapa/colecciones/{uuid}")
  public String eliminarColeccion(@PathVariable("uuid") UUID uuid, RedirectAttributes ra) {
    serviceColecciones.deleteColeccion(uuid);
    ra.addFlashAttribute("mensaje", "Colección eliminada correctamente");
    return "redirect:/metamapa/colecciones/";
  }

  //● Modificación del algoritmo de consenso.
  @PatchMapping("/metamapa/colecciones/{uuid}/consenso/{algoritmo:Absoluto|MultiplesMenciones|MayoriaSimple}")
  @ResponseBody
  public ResponseEntity<Void> modificarConsenso(@PathVariable UUID uuid,
                                                @PathVariable String algoritmo) {
    boolean ok = serviceColecciones.actualizarAlgoritmoConsenso(uuid, algoritmo);
    return ok ? ResponseEntity.noContent().build()   // 204
            : ResponseEntity.notFound().build();   // 404 si la colección no existe en el backend
  }

  //● Agregar fuentes de hechos de una colección.
  @PostMapping("/colecciones/{uuid}/fuente/{idFuente}")
  @ResponseBody
  public ResponseEntity<Void> agregarFuente(@PathVariable UUID uuid, @PathVariable Integer idFuente) {
      Coleccion coleccion = serviceColecciones.getColeccion(uuid);
      try {
          serviceAgregador.agregarFuente(idFuente);
          serviceAgregador.actualizarAgregador();
          return ResponseEntity.noContent().build();
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
      }
  }

  //Quitar fuentes de hechos de una colección
  @DeleteMapping("/colecciones/{uuid}/fuente/{idFuente}")
  @ResponseBody
  public ResponseEntity<Void> quitarFuente(@PathVariable UUID uuid, @PathVariable Integer idFuente) {
      Coleccion coleccion = serviceColecciones.getColeccion(uuid);
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
  enum Accion {APROBAR, RECHAZAR}

  @PatchMapping("/metamapa/solicitudes/{id}")
  @ResponseBody
  public ResponseEntity<Void> resolverSolicitud(@PathVariable UUID id,
                                                @RequestParam Accion accion) {
    var r = (accion == Accion.APROBAR)
            ? serviceAgregador.aprobarSolicitudEliminacion(id)
            : serviceAgregador.rechazarSolicitudEliminacion(id);

    return switch (r) {
      case OK -> ResponseEntity.noContent().build();
      case NOT_FOUND -> ResponseEntity.notFound().build();
      case CONFLICT -> ResponseEntity.status(409).build();
      default -> ResponseEntity.unprocessableEntity().build();
    };
  }

  // API Pública para otras instancias de MetaMapa
  //● Consulta de hechos dentro de una colección.
  @GetMapping("/metamapa/api/colecciones/{idColeccion}/hechos")
  public ArrayList<Hecho> consultarHechos(@PathVariable("idColeccion") UUID id) {
    return new ArrayList<>();
    //return serviceColecciones.getColeccion(idColeccion);
  }

  //● TODO Generar una solicitud de eliminación a un hecho.
  @PostMapping("/metamapa/api/solicitudesEliminacion/")
  public String generarSolicitudEliminacion(@RequestParam("hechoAfectado") String hechoAfectado,
                                            @RequestParam("motivo") String motivo,
                                            @RequestParam(value = "url", required = false) String url,
                                            RedirectAttributes ra ){
      Integer idSolicitud = serviceAgregador.crearSolicitudEliminacionYRetornarId(hechoAfectado, motivo, url);
      ra.addFlashAttribute("success", "Solicitud creada correctamente con id: " + idSolicitud);
      //return "redirect:/metamapa/solicitudesEliminacion/" + idSolicitud; //Te lleva a la pagina de la nueva solicitud
      return "redirect:/metamapa/solicitudesEliminacion/";
  }

  //● TODO: Navegación filtrada sobre una colección.
  @GetMapping("/metamapa/api/colecciones/{idColeccion}/hechos/")
  public List<Hecho> navegarFiltrado(
          @PathVariable UUID idColeccion,
          // filtros opcionales
          @RequestParam(required = false) String categoria,
          @RequestParam(required = false) String titulo,
          @RequestParam(required = false) String descripcion,
          // fechas (LocalDate)
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fecha_reporte_desde,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fecha_reporte_hasta,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fecha_acontecimiento_desde,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fecha_acontecimiento_hasta,
          // ubicación
          @RequestParam(required = false) Float ubicacion_latitud,
          @RequestParam(required = false) Float ubicacion_longitud,
          @RequestParam(required = false) Double radio_km, //Haversine en el agregador
          // otros
          @RequestParam(required = false) Integer id_fuente,
          @RequestParam(required = false) TipoMultimedia tipo_multimedia,

          // modo de navegación (default CURADA)
          @RequestParam(defaultValue = "CURADA") ModosDeNavegacion modo
  ) {
      return serviceColecciones.navegarFiltrado(
              idColeccion,
              modo,
              categoria,
              titulo,
              descripcion,
              fecha_reporte_desde,
              fecha_reporte_hasta,
              fecha_acontecimiento_desde,
              fecha_acontecimiento_hasta,
              ubicacion_latitud,
              ubicacion_longitud,
              radio_km,
              id_fuente,
              tipo_multimedia
      );
  }

    //● TODO: Navegación curada o irrestricta sobre una colección.
  @GetMapping("/metamapa/api/colecciones/{idColeccion}/hechos/{modo}")
  public ArrayList<Hecho> obtenerHechosNavegacion(@PathVariable("idColeccion") UUID id,
                                                  @PathVariable("modo") ModosDeNavegacion modosDeNavegacion) {
      Coleccion coleccion = serviceColecciones.getColeccion(id);
      return coleccion.getHechos(coleccion.getAgregador().getListaDeHechos(), modosDeNavegacion);
  }

  //● TODO: Reportar un hecho. Supongo que se refiere a crear una solicitud de edicion
  @PostMapping("/metamapa/api/solicitudesEdicion/")
  public String generarSolicitudEdicion(@RequestParam("hechoAfectado") String hechoAfectado,
                                            @RequestParam("motivo") String motivo,
                                            @RequestParam(value = "url", required = false) String url,
                                            RedirectAttributes ra ) {
      Integer idSolicitud = serviceAgregador.crearSolicitudEdicionYRetornarId(hechoAfectado, motivo, url);
      ra.addFlashAttribute("success", "Solicitud creada correctamente con id: " + idSolicitud);
      //return "redirect:/metamapa/solicitudesEliminacion/" + idSolicitud; //Te lleva a la pagina de la nueva solicitud
      return "redirect:/metamapa/solicitudesEdicion/";
  }

  @GetMapping("/metamapa/fuentesDeDatos/{id}")
  public String obtenerFuente(@PathVariable("id") Integer id, Model model) {
    model.addAttribute("fuente", serviceFuenteDeDatos.getFuenteDeDatos(id));
    return "fuenteDeDatos";
  }

  @GetMapping("/metamapa/fuentesDeDatos/")
  public String obtenerFuentes(Model model) {
    model.addAttribute("fuentesDeDatos", serviceFuenteDeDatos.getFuentesDeDatos());
    return "fuentesDeDatos";
  }

  /*
    @PostMapping(value = "/metamapa/fuentesDeDatos/", consumes = "application/json", produces = "application/json")
    public  ResponseEntity<String> crearFuenteDeDatos(@RequestBody String requestBody) {
      ResponseEntity<String> json = serviceFuenteDeDatos.crearFuente(requestBody);
      return ResponseEntity.status(json.getStatusCode()).headers(json.getHeaders()).body(json.getBody());

    }
  */
  @PostMapping(value = "/metamapa/fuentesDeDatos/")
  public String crearFuenteDeDatos(
          @RequestParam("tipo") String tipo,
          @RequestParam("nombre") String nombre,
          @RequestParam(value = "url", required = false) String url,
          RedirectAttributes ra
  ) {
    Integer idFuente = serviceFuenteDeDatos.crearFuenteYRetornarId(tipo, nombre, url);
    ra.addFlashAttribute("success", "Fuente creada correctamente con id: " + idFuente);
    //return "redirect:/metamapa/fuentesDeDatos/" + idFuente; //Te lleva a la pagina de la nueva fuente
    return "redirect:/metamapa/fuentesDeDatos/";
  }

  //TODO No necesitamos conectarnos con el agregador
  @GetMapping("/metamapa/agregador/hechos")
  public String obtenerHechosAgregador(Model model) {
    model.addAttribute("hechos", serviceAgregador.getAgregadorHechos());
    return "agregador";
  }

  @GetMapping("/metamapa/agregador/")
  public String obtenerAgregador(Model model) {
    model.addAttribute("agregador", serviceAgregador.getAgregador());
    return "agregador";
  }

  @PostMapping("/metamapa/agregador/fuentes/actualizar")
  public ResponseEntity<Void> actualizarAgregador() {
    serviceAgregador.actualizarAgregador();
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/metamapa/agregador/fuentesDeDatos/agregar/{idFuenteDeDatos}")
  public ResponseEntity<Void> agregarFuente(@PathVariable("idFuenteDeDatos") Integer idFuente) throws IOException {
    serviceAgregador.agregarFuente(idFuente);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/metamapa/agregador/fuentesDeDatos/remover/{idFuenteDeDatos}")
  public ResponseEntity<Void> removerFuente(@PathVariable("idFuenteDeDatos") Integer idFuente) throws IOException {
    serviceAgregador.removerFuente(idFuente);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/metamapa/fuentesDeDatos/{idFuenteDeDatos}/cargarCSV")
  public String cargarCSV(@PathVariable("idFuenteDeDatos") Integer idFuenteDeDatos,
                          @RequestParam("file") MultipartFile file, RedirectAttributes ra) throws IOException {
    serviceFuenteDeDatos.cargarCSV(idFuenteDeDatos, file);
    ra.addFlashAttribute("success", "CSV cargado correctamente");
    return "redirect:/metamapa/fuentesDeDatos/" + idFuenteDeDatos;
  }

  @PostMapping("/metamapa/fuentesDeDatos/{idFuenteDeDatos}/cargarHecho")
  public String cargarHecho(
          @PathVariable("idFuenteDeDatos") Integer idFuenteDeDatos,
          @RequestParam String titulo,
          @RequestParam(required = false) String descripcion,
          @RequestParam(required = false) String categoria,
          @RequestParam(required = false) Float latitud,
          @RequestParam(required = false) Float longitud,
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fechaHecho,
          @RequestParam(required = false) String autor,
          @RequestParam(name = "anonimo", defaultValue = "false") Boolean anonimo,
          @RequestParam(required = false) List<TipoMultimedia> tipoMultimedia,
          @RequestParam(required = false) List<String> path,
          RedirectAttributes ra
  ) {
    List<Multimedia> multimedia = new ArrayList<>();
    if (tipoMultimedia != null && path != null) {
      for (int i = 0; i < tipoMultimedia.size(); i++) {
        TipoMultimedia tipo = tipoMultimedia.get(i);
        String p = i < path.size() ? path.get(i).trim() : null;
        if (tipo != null && p != null && !p.isEmpty()) {
          Multimedia dto = new Multimedia();
          dto.setTipoMultimedia(tipo);
          dto.setPath(p);
          multimedia.add(dto);
        }
      }
    }
    serviceFuenteDeDatos.cargarHecho(
            idFuenteDeDatos,
            titulo,
            descripcion,
            categoria,
            latitud,
            longitud,
            fechaHecho,
            autor,
            anonimo,
            multimedia
    );
    ra.addFlashAttribute("success", "Hecho cargado correctamente");
    return "redirect:/metamapa/fuentesDeDatos/" + idFuenteDeDatos;
  }

  @GetMapping("/")
  public String redirectRoot() {
    return "redirect:/metamapa";
  }

  @GetMapping("/metamapa")
  public String mostrarHome(Model model) {
    return "home";
  }
}