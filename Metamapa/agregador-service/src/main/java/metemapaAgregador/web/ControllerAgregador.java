package metemapaAgregador.web;


import domain.business.Agregador.Agregador;
import domain.business.incidencias.Hecho;
import java.util.ArrayList;

import metemapaAgregador.Service.ServiceFuenteDeDatos;
import metemapaAgregador.persistencia.RepositorioAgregador;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api-agregador")
public class ControllerAgregador {
  private final ServiceFuenteDeDatos servicefuenteDeDatos;
  private final RepositorioAgregador repositorioAgregador = new RepositorioAgregador();

  public ControllerAgregador(ServiceFuenteDeDatos servicefuenteDeDatos) {
    this.servicefuenteDeDatos = servicefuenteDeDatos;
  }


/*
  public void guardarHechos(int idFuente)
  {
    ArrayList<Map<String,Object>> hechos = servicefuenteDeDatos.getHechosDeFuente(idFuente);

    hechos.forEach(h -> repositorioAgregador.persistirHechos(h));
  }
*/
  public void actualizarHechos()
  {
    /*
    ArrayList<Integer> fuentes = repositorioAgregador.getFuentes();

    fuentes.forEach(f -> guardarHechos(f));
    */
    //TODO este metodo ya lo tiene el agregador
    repositorioAgregador.getAgregador().actualizarHechos();
  }

  @GetMapping("/")
  public ResponseEntity<Agregador> getAgregador() {
    Agregador agregador = repositorioAgregador.getAgregador();
    if (agregador == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(agregador);
  }


  @PostMapping ("/fuentes/actualizar")
  public ResponseEntity<Void> actualizarAgregador() {
    try {
      var fuentes = servicefuenteDeDatos.obtenerFuenteDeDatos();
      if (fuentes == null || fuentes.isEmpty()) {
        return ResponseEntity.noContent().build();
      }
      repositorioAgregador.getAgregador().actualizarFuentesDeDatos(fuentes);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      // Se puede usar un logger para loguear el error
      return ResponseEntity.status(500).build();
    }
  }


  //TODO esto se va a comunicar con el servicio de colecciones
  //TODO y las colecciones filtran estos hechos
  @GetMapping("/hechos")
  public ResponseEntity<ArrayList<Hecho>> getAgregadorHechos() {
    ArrayList<Hecho> hechos = repositorioAgregador.getAgregador().getListaDeHechos();

    if (hechos == null || hechos.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(hechos);
  }


  @PostMapping ("/fuentesDeDatos/agregar/{idFuente}")
  public ResponseEntity<Void> agregarFuente(@PathVariable int idFuente) {
    try {
      var fuente = servicefuenteDeDatos.getFuenteDeDatos(idFuente);
      if (fuente == null) {
        return ResponseEntity.notFound().build();
      }
      repositorioAgregador.getAgregador().agregarFuenteDeDatos(fuente);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }

  @PostMapping ("/fuentesDeDatos/remover/{idFuente}")
  public ResponseEntity<Void> eliminarFuente(@PathVariable int idFuente) {
    try {
      repositorioAgregador.getAgregador().removerFuenteDeDatos(idFuente);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
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