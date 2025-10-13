package Agregador.web;
import Agregador.business.Hechos.Hecho;
import java.util.*;
import Agregador.Service.ServiceFuenteDeDatos;
import Agregador.persistencia.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Agregador.Service.ServiceConsenso;

@RestController
@RequestMapping("/api-agregador")
public class ControllerAgregador {
  private final ServiceFuenteDeDatos servicefuenteDeDatos;
  private final RepositorioHechos repositorioHechos;
  private final ServiceConsenso serviceConsenso;
  private final Set<String> URLsFuentes = new HashSet<>();

  public ControllerAgregador(ServiceFuenteDeDatos servicefuenteDeDatos, ServiceConsenso serviceConsenso, RepositorioHechos repositorioHechos) {
    this.servicefuenteDeDatos = servicefuenteDeDatos;
    this.repositorioHechos = repositorioHechos;
    this.serviceConsenso = serviceConsenso;
  }

  /*  public void guardarHechos(int idFuente){
      ArrayList<Map<String,Object>> hechos = servicefuenteDeDatos.getHechosDeFuente(idFuente);

      hechos.forEach(h -> repositorioAgregador.persistirHechos(h));
    }*/

  @PostMapping("/fuenteDeDatos")
  public ResponseEntity<String> agregarFuente(@RequestBody Map<String, Object> body) {
    String url = (String) body.get("url");
    if (url == null) {
      return ResponseEntity.noContent().build();
    }
    URLsFuentes.add(url);
    System.out.println("Agregando fuente de datos: " + url);
    //imprimir lista de URLs
    System.out.println("Lista de URLs: " + URLsFuentes);
    return ResponseEntity.ok(url);
  }

  @PostMapping("/actualizarHechos")
  public ResponseEntity<?> actualizarHechos() {
    System.out.println("Actualizando hechos de las fuentes de datos!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    URLsFuentes.forEach(servicefuenteDeDatos::actualizarHechos);
    return ResponseEntity.ok("Se actualizaron los hechos");
  }

  @PostMapping("/consensuarHechos")
  public ResponseEntity<?> consensuarHechos() {
    serviceConsenso.consensuarHechos();
    return ResponseEntity.ok("Se consensuaron los hechos");
  }

  // Listar todos los hechos
  @GetMapping("/hechos")
  public ResponseEntity<List<Hecho>> getAgregadorHechos() {
    return ResponseEntity.ok(repositorioHechos.findAll());
  }

//  @PostMapping("/api-agregador/fuentes/actualizar")
//  public ResponseEntity<Void> actualizarAgregador() {
//    var fuentes = servicefuenteDeDatos.obtenerFuenteDeDatos();
//    if (fuentes == null || fuentes.isEmpty()) return ResponseEntity.noContent().build();
//    repositorioAgregador.getAgregador().actualizarFuentesDeDatos(fuentes);
//    return ResponseEntity.noContent().build();
//  }
}