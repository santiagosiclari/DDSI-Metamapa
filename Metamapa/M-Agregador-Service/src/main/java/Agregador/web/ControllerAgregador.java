package Agregador.web;
import Agregador.DTO.FiltrosHechosDTO;
import Agregador.Service.ServiceAgregador;
import Agregador.Service.ServiceFuenteDeDatos;
import Agregador.Service.ServiceConsenso;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.*;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.slf4j.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-agregador")
public class ControllerAgregador {
  private final ServiceFuenteDeDatos servicefuenteDeDatos;
  private final RepositorioHechos repositorioHechos;
  private final ServiceConsenso serviceConsenso;
  private final ServiceAgregador serviceAgregador;
  private final Map<String,String> URLsFuentes = new HashMap<>();
  private static final Logger log = LoggerFactory.getLogger(ControllerAgregador.class);

  @PostMapping("/fuenteDeDatos")
  public ResponseEntity<String> agregarFuente(@RequestBody Map<String, Object> body) {
    String url = (String) body.get("url");
    String tipoFuente = (String) body.get("tipoFuente");
    if (url == null) {
      return ResponseEntity.noContent().build();
    }
    URLsFuentes.put(url, tipoFuente);
    log.info("Agregando fuente de datos: {} tipoFuente: {}", url, tipoFuente);
    log.debug("Lista de URLs: {}", URLsFuentes);
    return ResponseEntity.ok(url);
  }

  @GetMapping("/fuenteDeDatos")
  public ResponseEntity<Map<String,String>> getFuentes() {
    return ResponseEntity.ok(URLsFuentes);
  }

  @PostMapping("/actualizarHechos")
  public ResponseEntity<?> actualizarHechos() {
    URLsFuentes.keySet().forEach(servicefuenteDeDatos::actualizarHechos);
    return ResponseEntity.ok("Se actualizaron los hechos");
  }

  @PostMapping("/consensuarHechos")
  public ResponseEntity<?> consensuarHechos() {
    serviceConsenso.consensuarHechos();
    return ResponseEntity.ok("Se consensuaron los hechos");
  }

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

  /*@GetMapping("/hechos/buscar")
  public ResponseEntity<?> buscarHechosPorTextoLibre(
          @RequestParam String texto, // Se espera el texto de búsqueda (requerido)
          @PageableDefault(size = 500, page = 0) Pageable pageable
  ) {
    if (texto.isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error", "El texto de búsqueda no puede estar vacío."));
    }

    try {
      // Delega al servicio la búsqueda por texto libre con paginación
      List<Hecho> hechos = serviceAgregador.obtenerHechos(texto, pageable);

      return ResponseEntity.ok(hechos);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error", "Error al ejecutar la búsqueda de texto libre: " + e.getMessage()));
    }
  }*/
}