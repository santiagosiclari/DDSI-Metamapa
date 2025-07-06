package Metamapa.web;


import Metamapa.service.ServiceFuenteDeDatos;
import Metamapa.service.ServiceAgregador;
import domain.business.incidencias.Hecho;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ControllerMetamapa {

  private final ServiceFuenteDeDatos servicefuenteDeDatos;
  private final ServiceAgregador serviceAgregador;

  public ControllerMetamapa(ServiceFuenteDeDatos servicefuenteDeDatos,
                            ServiceAgregador serviceAgregador) {
    this.servicefuenteDeDatos = servicefuenteDeDatos;
    this.serviceAgregador = serviceAgregador;
  }

  @GetMapping("/metamapa/fuenteDeDatos/{id}/hechos")
  public String mostrarFuente(@PathVariable("id") Integer id, Model model) {
    model.addAttribute("fuente", servicefuenteDeDatos.getFuenteDeDatos(id));
    return "fuenteDeDatos";
  }


  //TODO No necesitamos conectarnos con el agregador
  @GetMapping("/metamapa/agregador/{id}/hechos")
  public String mostrarAgregador(@PathVariable("id") Integer id, Model model) {
    model.addAttribute("agregador", serviceAgregador.getAgregador(id));
    return "agregador";
  }

  @GetMapping ("/metamapa/colecciones/{handler}/hechos")
  public String mostrarColeccion(@PathVariable("handler")UUID handler,)


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
