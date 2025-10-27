package FuenteDemo.web;
import FuenteDemo.business.FuentesDeDatos.FuenteDemo;
import FuenteDemo.business.Hechos.Hecho;
import FuenteDemo.persistencia.RepositorioHechos;
import FuenteDemo.service.ServiceFuenteDemo;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import FuenteDemo.DTO.HechoDTO;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteDemo {
  private final ServiceFuenteDemo fuenteDemoService;
  private final RepositorioHechos repositorioHechos;

  public ControllerFuenteDemo(ServiceFuenteDemo fuenteDemoService, RepositorioHechos repositorioHechos) {
    this.fuenteDemoService = fuenteDemoService;
    this.repositorioHechos = repositorioHechos;
  }

  // Obtener todas las fuentes
  @GetMapping("/")
  public ResponseEntity<List<FuenteDemo>> getFuentesDeDatos() {
    return ResponseEntity.ok(fuenteDemoService.getFuentes());
  }

  @GetMapping("/{idFuenteDeDatos}")
  public ResponseEntity<FuenteDemo> getFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return ResponseEntity.ok(fuenteDemoService.obtenerFuente(idfuenteDeDatos));
  }

  // Crear una fuente
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> crearFuenteDeDatos(@RequestBody Map<String, Object> requestBody) {
    try {
      FuenteDemo fuente = fuenteDemoService.crearFuente(requestBody);
      return ResponseEntity.ok(fuente);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
    }
  }

  // Obtener hechos de una fuente
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ResponseEntity<List<HechoDTO>> obtenerHechos(@PathVariable Integer idFuenteDeDatos) {
    return ResponseEntity.ok(fuenteDemoService.obtenerHechos(idFuenteDeDatos).stream()
        .map(h -> new HechoDTO(h))
        .toList());
  }

  //obtener todos los hechos
  @GetMapping("/hechos")
  public ResponseEntity<List<HechoDTO>> obtenerTodosLosHechos() {
    List<Hecho> todosLosHechos = repositorioHechos.findAll();

    return ResponseEntity.ok(todosLosHechos.stream()
        .map(h -> new HechoDTO(h))
        .toList());
  }
}