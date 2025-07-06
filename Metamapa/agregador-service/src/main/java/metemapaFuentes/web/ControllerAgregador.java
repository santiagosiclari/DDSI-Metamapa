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

  @GetMapping("/hechos")
  public Agregador  getAgregador(
      @PathVariable(value = "idAgregador") Integer idAgregador) {
    return repositorioAgregador.buscarAgregador(idAgregador);
  }
}

/*

@PatchMapping(value = "/solicitudesElimincacion/{id}", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity actualizarEstadoSolicitud(@PathVariable("id") String id, @RequestBody Map<String, Object> requestBody) {
    try {
      Optional<SolicitudEliminacion> solicitudOpt = solicitudRepository.findById(id);
      if (solicitudOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }

*/