package Agregador.web;
import Agregador.DTO.FiltrosHechosDTO;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Hechos.Hecho;
import jakarta.validation.Valid;
import java.util.*;
import Agregador.Service.ServiceFuenteDeDatos;
import Agregador.persistencia.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Agregador.Service.ServiceConsenso;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-agregador")
public class ControllerAgregador {
  private final ServiceFuenteDeDatos servicefuenteDeDatos;
  private final RepositorioHechos repositorioHechos;
  private final ServiceConsenso serviceConsenso;
  private final Map<String,String> URLsFuentes = new HashMap<>();

  /* public void guardarHechos(int idFuente){
      ArrayList<Map<String,Object>> hechos = servicefuenteDeDatos.getHechosDeFuente(idFuente);

      hechos.forEach(h -> repositorioAgregador.persistirHechos(h));
  }*/

  @PostMapping("/fuenteDeDatos")
  public ResponseEntity<String> agregarFuente(@RequestBody Map<String, Object> body) {
    String url = (String) body.get("url");
    String tipoFuente = (String) body.get("tipoFuente");
    if (url == null) {
      return ResponseEntity.noContent().build();
    }
    URLsFuentes.put(url, tipoFuente);
    System.out.println("Agregando fuente de datos: " + url + " tipoFuente: " + tipoFuente);
    //imprimir lista de URLs
    System.out.println("Lista de URLs: " + URLsFuentes);
    return ResponseEntity.ok(url);
  }

  @GetMapping("/fuenteDeDatos")
  public ResponseEntity<Map<String,String>> getFuentes() {
    return ResponseEntity.ok(URLsFuentes);
  }

  @PostMapping("/actualizarHechos")
  public ResponseEntity<?> actualizarHechos() {
    URLsFuentes.keySet().forEach(servicefuenteDeDatos::actualizarHechos);


    //URLsFuentes.forEach(servicefuenteDeDatos::actualizarHechos);
    return ResponseEntity.ok("Se actualizaron los hechos");
  }

  @PostMapping("/consensuarHechos")
  public ResponseEntity<?> consensuarHechos() {
    serviceConsenso.consensuarHechos();
    return ResponseEntity.ok("Se consensuaron los hechos");
  }

  /*
    // Listar todos los hechos
    @GetMapping("/hechos")
    public ResponseEntity<List<Hecho>> getAgregadorHechos() {
      return ResponseEntity.ok(repositorioHechos.findAll());
    }*/

  // Listar todos los hechos filtrados
  @GetMapping("/hechos")
  public ResponseEntity<?> getAgregadorHechos(@Valid FiltrosHechosDTO filtros) {
    try {
      List<Criterio> criterios = new ArrayList<>();
      criterios.addAll(repositorioHechos.construirCriterios(filtros, true));
      criterios.addAll(repositorioHechos.construirCriterios(filtros, false));
      if (criterios.isEmpty()) return ResponseEntity.ok(repositorioHechos.findAll());

      List<Hecho> filtrados = repositorioHechos.filtrarPorCriterios(criterios, null);
      return ResponseEntity.ok(filtrados);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

}