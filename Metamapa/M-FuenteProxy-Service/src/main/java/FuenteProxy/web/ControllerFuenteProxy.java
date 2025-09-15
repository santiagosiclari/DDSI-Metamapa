package java.FuenteProxy.web;
import java.FuenteProxy.business.FuentesDeDatos.*;
import java.FuenteProxy.business.Hechos.Hecho;
import java.util.*;
import java.FuenteProxy.service.ServiceFuenteProxy;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteProxy{
  private final ServiceFuenteProxy fuenteProxyService;

  public ControllerFuenteProxy(ServiceFuenteProxy fuenteProxyService) {
    this.fuenteProxyService = fuenteProxyService;
  }

  // Obtener todas las fuentes
  @GetMapping("/")
  public ResponseEntity<List<FuenteProxy>> getFuentesDeDatos() {
    return ResponseEntity.ok(fuenteProxyService.getFuentes());
  }

  // Obtener una fuente por id
  @GetMapping("/{idFuenteDeDatos}")
  public FuenteProxy getFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return fuenteProxyService.obtenerFuente(idfuenteDeDatos);
  }

  // Crear una fuente
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> crearFuenteDeDatos(@RequestBody Map<String, Object> requestBody) {
    try {
      FuenteProxy fuente = fuenteProxyService.crearFuente(requestBody);
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
    return ResponseEntity.ok(fuenteProxyService.obtenerHechos(idFuenteDeDatos));
  }




}