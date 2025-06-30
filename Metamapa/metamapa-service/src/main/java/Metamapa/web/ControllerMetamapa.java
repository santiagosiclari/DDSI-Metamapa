package Metamapa.web;


import Metamapa.service.ServiceFuenteDeDatos;
import Metamapa.service.ServiceAgregador;
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

  @GetMapping("/metamapa/agregador/{id}/hechos")
  public String mostrarAgregador(@PathVariable("id") Integer id, Model model) {
    model.addAttribute("agregador", serviceAgregador.getAgregador(id));
    return "agregador";
  }
  @GetMapping("/metamapa/asd")
  public String mostrarAgregador(Model model) {
    return "asd";
  }
}
