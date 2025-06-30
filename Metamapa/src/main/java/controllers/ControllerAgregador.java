package controllers;

import domain.Persistencia.RepositorioAgregador;
import domain.Persistencia.RepositorioFuentes;
import domain.business.Agregador.Agregador;
import domain.business.FuentesDeDatos.FuenteDeDatos;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@SpringBootApplication
@Controller

public class ControllerAgregador {
  public RepositorioAgregador repositorioAgregador = new RepositorioAgregador();
  public static void main(String[] args) {

    //SpringApplication.run(testApplication.class, args);
    SpringApplication app = new SpringApplication(controllers.ControllerAgregador.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "9008"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }

  @GetMapping("/Agregador/{idAgregador}/hechos")
  public String  getAgregador(
      @PathVariable(value = "idAgregador") Integer idAgregador,
      Model model) {
    Agregador agregador = repositorioAgregador.buscarAgregador(idAgregador);
    model.addAttribute("agregador", agregador);
    return "agregador";
  }
}