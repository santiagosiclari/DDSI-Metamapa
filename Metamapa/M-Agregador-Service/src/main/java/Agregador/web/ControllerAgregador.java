package Agregador.web;
import Agregador.business.Agregador.Agregador;
import Agregador.business.Colecciones.*;
import Agregador.business.Hechos.Hecho;
import java.util.*;
import Agregador.Service.ServiceFuenteDeDatos;
import Agregador.persistencia.RepositorioAgregador;
import Agregador.persistencia.RepositorioColecciones;
import Agregador.persistencia.RepositorioHechos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api-agregador")
public class ControllerAgregador {
  private final ServiceFuenteDeDatos servicefuenteDeDatos;
  private final RepositorioAgregador repositorioAgregador = new RepositorioAgregador();
  private final RepositorioColecciones repositorioColecciones;
  private final RepositorioHechos repositorioHechos = new RepositorioHechos();
  public ControllerAgregador(ServiceFuenteDeDatos servicefuenteDeDatos, RepositorioColecciones repositorioColecciones) {
    this.servicefuenteDeDatos = servicefuenteDeDatos;
    this.repositorioColecciones = repositorioColecciones;
  }

  /*
    public void guardarHechos(int idFuente){
      ArrayList<Map<String,Object>> hechos = servicefuenteDeDatos.getHechosDeFuente(idFuente);

      hechos.forEach(h -> repositorioAgregador.persistirHechos(h));
    }
  */

  private ArrayList<String> obtenerURLFuentes() {
    ArrayList<String> URLsFuentes = new ArrayList<String>();
    URLsFuentes.add("${M.FuenteDinamica.service.url}");
    URLsFuentes.add("${M.FuenteEstatica.service.url}");
    URLsFuentes.add("${M.FuenteProxy.service.url}");
    return URLsFuentes;
  }

  public void actualizarHechos() {
    ArrayList<String> URLsFuentes = obtenerURLFuentes();
    ArrayList<Hecho> hechos = new ArrayList<>();
    URLsFuentes.forEach(url -> {
      hechos.addAll(new ServiceFuenteDeDatos(new RestTemplate(), url, repositorioHechos).getHechos());
    });
    repositorioAgregador.getAgregador().actualizarHechos(hechos);
  }

  public void consensuarHechos() {
    //TODO implementar que dispare procedures en la BBDD
  }

  @GetMapping("/")
  public ResponseEntity<Agregador> getAgregador() {
    Agregador agregador = repositorioAgregador.getAgregador();
    if (agregador == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(agregador);
  }

//  @PostMapping ("/fuentes/actualizar")
//  public ResponseEntity<?> actualizarAgregador() {
//    try {
//      var fuentes = servicefuenteDeDatos.obtenerFuenteDeDatos();
//      if (fuentes == null || fuentes.isEmpty()) {
//        return ResponseEntity.noContent().build();
//      }
//      repositorioAgregador.getAgregador().actualizarFuentesDeDatos(fuentes);
//      return ResponseEntity.noContent().build();
//    } catch (Exception e) {
//      return ResponseEntity.status(500).build();
//    }
//  }

  //TODO esto se va a comunicar con el servicio de colecciones
  // y las colecciones filtran estos hechos
//  @GetMapping("/hechos")
//  public ResponseEntity<ArrayList<Hecho>> getAgregadorHechos() {
//    ArrayList<Hecho> hechos = repositorioAgregador.getAgregador().getListaDeHechos();
//
//    if (hechos == null || hechos.isEmpty()) {
//      return ResponseEntity.noContent().build();
//    }
//    return ResponseEntity.ok(hechos);
//  }

  //TODO: estos 2 no  deberian ir en el controller colecciones?
  @PostMapping("/fuentesDeDatos/{idColeccion}/{idFuente}")
  public ResponseEntity<Void> agregarFuente(@PathVariable Integer idFuente, @PathVariable String idColeccion) {
    try {
      Coleccion col = repositorioColecciones.buscarXUUID(UUID.fromString(idColeccion))
                      .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
      col.agregarCriterioPertenencia(new CriterioFuenteDeDatos(idFuente));
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }
  @PostMapping("/fuentesDeDatos/{idColeccion}/remover/{idFuente}")
  public ResponseEntity<Void> eliminarFuente(@PathVariable Integer idFuente,@PathVariable String idColeccion) {
    try {
      Coleccion col = repositorioColecciones.buscarXUUID(UUID.fromString(idColeccion))
              .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
      if (col == null) return ResponseEntity.notFound().build();
      col.eliminarCriterioPertenencia(new CriterioFuenteDeDatos(idFuente));
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }
}