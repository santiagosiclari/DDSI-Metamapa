package FuenteMetamapa.web;
import FuenteMetamapa.business.FuentesDeDatos.*;
import FuenteMetamapa.business.Hechos.Hecho;
import java.util.*;
import FuenteMetamapa.service.ServiceFuenteMetamapa;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class ControllerFuenteMetamapa{
  private final ServiceFuenteMetamapa fuenteMetamapaService;

  public ControllerFuenteMetamapa(ServiceFuenteMetamapa fuenteMetamapaService) {
    this.fuenteMetamapaService = fuenteMetamapaService;
  }

  // Obtener todas las fuentes
  @GetMapping("/")
  public ResponseEntity<List<FuenteMetamapa>> getFuentesDeDatos() {
    return ResponseEntity.ok(fuenteMetamapaService.getFuentes());
  }

  @GetMapping("/{idFuenteDeDatos}")
  public FuenteMetamapa getFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return fuenteMetamapaService.obtenerFuente(idfuenteDeDatos);
  }

  // Crear una fuente
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> crearFuenteDeDatos(@RequestBody Map<String, Object> requestBody) {
    try {
      FuenteMetamapa fuente = fuenteMetamapaService.crearFuente(requestBody);
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
    return ResponseEntity.ok(fuenteMetamapaService.obtenerHechos(idFuenteDeDatos));
  }

}