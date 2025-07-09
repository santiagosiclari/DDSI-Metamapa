package Metamapa.web;
import Metamapa.Service.ServiceFuenteDeDatos;
import Metamapa.Service.ServiceAgregador;
import Metamapa.Service.ServiceIncidencias;
import domain.business.incidencias.Hecho;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

  @GetMapping("/metamapa/fuentesDeDatos/{id}")
  public String mostrarFuente(@PathVariable("id") Integer id, Model model) {
    model.addAttribute("fuente", serviceFuenteDeDatos.getFuenteDeDatos(id));
    return "fuenteDeDatos";
  }


  //TODO No necesitamos conectarnos con el agregador
  @GetMapping("/metamapa/agregador/hechos")
  public String mostrarAgregador(Model model) {
    model.addAttribute("hechos", serviceAgregador.getAgregadorHechos());
    return "agregador";
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
  public ResponseEntity<Void>
  cargarCSV(
      @PathVariable("idFuenteDeDatos") Integer idFuente,
      @RequestParam("file") MultipartFile file) throws IOException
  {
    serviceFuenteDeDatos.cargarCSV(idFuente,file);
    return ResponseEntity.ok().build();
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