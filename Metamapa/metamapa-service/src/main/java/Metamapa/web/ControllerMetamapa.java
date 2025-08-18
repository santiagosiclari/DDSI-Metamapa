package Metamapa.web;
import Metamapa.service.*;
import Metamapa.business.Hechos.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControllerMetamapa {
  private final ServiceFuenteDeDatos serviceFuenteDeDatos;
  private final ServiceAgregador serviceAgregador;
  private final ServiceIncidencias serviceIncidencias;
  private final ServiceColecciones serviceColecciones;

  public ControllerMetamapa(ServiceFuenteDeDatos serviceFuenteDeDatos,
                            ServiceAgregador serviceAgregador,
                            ServiceIncidencias serviceIncidencias,
                            ServiceColecciones serviceColecciones) {
    this.serviceFuenteDeDatos = serviceFuenteDeDatos;
    this.serviceAgregador = serviceAgregador;
    this.serviceIncidencias = serviceIncidencias;
    this.serviceColecciones = serviceColecciones;
  }

  @ExceptionHandler(Exception.class)
  public String handleAllExceptions(Exception ex, Model model) {
    model.addAttribute("errorMessage", ex.getMessage());
    return "error";  // Thymeleaf buscará templates/error.html
  }

  // API Administrativa de MetaMapa
  //● Operaciones CRUD sobre las colecciones.
  @GetMapping("/metamapa/colecciones/")
  public String obtenerColecciones(Model model) {
    model.addAttribute("colcecciones", serviceColecciones.getColecciones());
    return "colecciones";
  }

  @GetMapping("/metamapa/colecciones/{uuid}")
  public String obtenerColeccion(@PathVariable("uuid") UUID uuid, Model model) {
    model.addAttribute("colceccion", serviceColecciones.getColeccion(uuid));
    return "coleccion";
  }

  //● Modificación del algoritmo de consenso.

  //● Agregar o quitar fuentes de hechos de una colección.

  //● Aprobar o denegar una solicitud de eliminación de un hecho.

  // API Pública para otras instancias de MetaMapa
  //● Consulta de hechos dentro de una colección.
  //@GetMapping ("/metamapa/colecciones/{id}/hechos")
  //public String mostrarColeccion(@PathVariable("handler")UUID handler,)
  @GetMapping("/metamapa/api/colecciones/{idColeccion}/hechos")
  public ArrayList<Hecho> consultarHechos(@PathVariable("idColeccion") Integer id) {
    return new ArrayList<>();
    //return serviceColecciones.getColeccion(idColeccion);
  }

  //● Generar una solicitud de eliminación a un hecho.

  //● Navegación filtrada sobre una colección.

  //● Navegación curada o irrestricta sobre una colección.

  //● Reportar un hecho.


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