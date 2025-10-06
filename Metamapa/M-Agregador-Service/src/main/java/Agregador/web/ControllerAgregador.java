package Agregador.web;
import Agregador.Service.ServiceAgregador;
import Agregador.business.Consenso.Consenso;
import Agregador.business.Hechos.Hecho;
import java.util.*;
import Agregador.Service.ServiceFuenteDeDatos;
import Agregador.persistencia.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import Agregador.Service.ServiceConsenso;

@RestController
@RequestMapping("/api-agregador")
public class ControllerAgregador {
  private final ServiceFuenteDeDatos servicefuenteDeDatos;
  private final RepositorioAgregador repositorioAgregador;
  private final RepositorioHechos repositorioHechos;
  private final ServiceAgregador serviceAgregador;
  private final ServiceConsenso serviceConsenso;
  private ArrayList<String> URLsFuentes = new ArrayList<String>();

  public ControllerAgregador(ServiceFuenteDeDatos servicefuenteDeDatos,ServiceConsenso serviceConsenso,
                             RepositorioAgregador repositorioAgregador, ServiceAgregador serviceAgregador,
                                RepositorioHechos repositorioHechos) {
    this.servicefuenteDeDatos = servicefuenteDeDatos;
    this.repositorioAgregador = repositorioAgregador;
    this.repositorioHechos = repositorioHechos;
    this.serviceAgregador = serviceAgregador;
    this.serviceConsenso = serviceConsenso;
  }

  /*
    public void guardarHechos(int idFuente){
      ArrayList<Map<String,Object>> hechos = servicefuenteDeDatos.getHechosDeFuente(idFuente);

      hechos.forEach(h -> repositorioAgregador.persistirHechos(h));
    }
  */
  @PostMapping("/fuenteDeDatos")
  public ResponseEntity<String> agregarFuente(@RequestBody Map<String,Object> body) {
    String url = (String)body.get("url");
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
    URLsFuentes.forEach(servicefuenteDeDatos::actualizarHechos);
    return ResponseEntity.ok("Se actualizaron los hechos");
  }

  public void consensuarHechos() {
    serviceConsenso.consensuarHechos();
  }

  // Listar todos los hechos
  @GetMapping("/hechos")
  public ResponseEntity<List<Hecho>> getAgregadorHechos() {
    return ResponseEntity.ok(repositorioHechos.findAll());
  }

  /*@GetMapping("/")
  public ResponseEntity<Agregador> getAgregador() {
    Agregador agregador = repositorioAgregador.getAgregador();
    if (agregador == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(agregador);
  }*/


//  @PostMapping("/api-agregador/fuentes/actualizar")
//  public ResponseEntity<Void> actualizarAgregador() {
//    var fuentes = servicefuenteDeDatos.obtenerFuenteDeDatos();
//    if (fuentes == null || fuentes.isEmpty()) return ResponseEntity.noContent().build();
//    repositorioAgregador.getAgregador().actualizarFuentesDeDatos(fuentes);
//    return ResponseEntity.noContent().build();
//  }
}