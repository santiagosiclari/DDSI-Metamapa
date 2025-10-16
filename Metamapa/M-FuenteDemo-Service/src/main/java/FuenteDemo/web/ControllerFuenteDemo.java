package FuenteDemo.web;
import FuenteDemo.business.FuentesDeDatos.FuenteDemo;
import FuenteDemo.business.Hechos.Hecho;
import FuenteDemo.service.ServiceFuenteDemo;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteDemo {
  private final ServiceFuenteDemo fuenteDemoService;

  public ControllerFuenteDemo(ServiceFuenteDemo fuenteDemoService) {
    this.fuenteDemoService = fuenteDemoService;
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
  public ResponseEntity<List<Hecho>> obtenerHechos(@PathVariable Integer idFuenteDeDatos) {
    return ResponseEntity.ok(fuenteDemoService.obtenerHechos(idFuenteDeDatos));
  }

  //obtener todos los hechos
  @GetMapping("/hechos")
  public ResponseEntity<List<Hecho>> obtenerTodosLosHechos() {
    List<Hecho> todosLosHechos = new ArrayList<>();
    for (FuenteDemo fuente : fuenteDemoService.getFuentes()) {
      todosLosHechos.addAll(fuente.getHechos());
    }
    return ResponseEntity.ok(todosLosHechos);
  }
}