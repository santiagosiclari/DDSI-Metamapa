package metemapaFuentes.web;

import domain.business.Agregador.Agregador;
import metemapaFuentes.persistencia.RepositorioAgregador;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agregador")

public class ControllerAgregador {

  public RepositorioAgregador repositorioAgregador = new RepositorioAgregador();

  @GetMapping("/{idAgregador}/hechos")
  public Agregador  getAgregador(
      @PathVariable(value = "idAgregador") Integer idAgregador) {
    return repositorioAgregador.buscarAgregador(idAgregador);
  }
}