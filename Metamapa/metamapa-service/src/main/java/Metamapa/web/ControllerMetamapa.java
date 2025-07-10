package Metamapa.web;
import Metamapa.Service.ServiceFuenteDeDatos;
import Metamapa.Service.ServiceAgregador;
import Metamapa.Service.ServiceIncidencias;
import domain.business.FuentesDeDatos.FuenteDemo;
import domain.business.FuentesDeDatos.FuenteEstatica;
import domain.business.FuentesDeDatos.FuenteMetamapa;
import domain.business.incidencias.Hecho;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControllerMetamapa {
  private final ServiceFuenteDeDatos serviceFuenteDeDatos;
  private final ServiceAgregador serviceAgregador;
  private final ServiceIncidencias serviceIncidencias;

  public ControllerMetamapa(ServiceFuenteDeDatos serviceFuenteDeDatos,
                            ServiceAgregador serviceAgregador,
                            ServiceIncidencias  serviceIncidencias) {
    this.serviceFuenteDeDatos = serviceFuenteDeDatos;
    this.serviceAgregador = serviceAgregador;
    this.serviceIncidencias = serviceIncidencias;
  }

  @ExceptionHandler(Exception.class)
  public String handleAllExceptions(Exception ex, Model model) {
    model.addAttribute("errorMessage", ex.getMessage());
    return "error";  // Thymeleaf buscará templates/error.html
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
    @RequestParam(value="url", required=false) String url,
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
  public ResponseEntity<Void>
  agregarFuente(
      @PathVariable("idFuenteDeDatos") Integer idFuente) throws IOException
  {
    serviceAgregador.agregarFuente(idFuente);
    return ResponseEntity.ok().build();
  }
  @PostMapping("/metamapa/agregador/fuentesDeDatos/remover/{idFuenteDeDatos}")
  public ResponseEntity<Void>
  removerFuente(
      @PathVariable("idFuenteDeDatos") Integer idFuente) throws IOException
  {
    serviceAgregador.removerFuente(idFuente);
    return ResponseEntity.ok().build();
  }
  @PostMapping("/metamapa/fuentesDeDatos/{idFuenteDeDatos}/cargarCSV")
  public String
  cargarCSV(
      @PathVariable("idFuenteDeDatos") Integer idFuenteDeDatos,
      @RequestParam("file") MultipartFile file, RedirectAttributes ra) throws IOException
  {
    serviceFuenteDeDatos.cargarCSV(idFuenteDeDatos,file);
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
      @RequestParam(required = false) String multimedia,
      RedirectAttributes ra
  ) {
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


  //@GetMapping ("/metamapa/colecciones/{id}/hechos")
  //public String mostrarColeccion(@PathVariable("handler")UUID handler,)


  @GetMapping("/")
  public String redirectRoot() {
    return "redirect:/metamapa";
  }

  @GetMapping("/metamapa")
  public String mostrarHome(Model model) {
    return "home";
  }

  @GetMapping("/metamapa/consultas")
  public String mostrarConsultas(Model model) {
    return "consultas";
  }
  //API
  @GetMapping("/metamapa/api/colecciones/{idColeccion}/hechos")
  public ArrayList<Hecho> consultarHechos (@PathVariable ("idColeccion")Integer id) {
  return new ArrayList<>();
    //return serviceColecciones.getColeccion(idColeccion);
  }
}