package FuenteProxy.web;
import FuenteProxy.business.FuentesDeDatos.*;
import FuenteProxy.business.Hechos.Hecho;
import java.util.List;
import java.util.Map;
import FuenteProxy.service.ServiceFuenteProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteProxy{
  private final ServiceFuenteProxy fuenteProxyService;

  public ControllerFuenteProxy(ServiceFuenteProxy fuenteProxyService) {
    this.fuenteProxyService = fuenteProxyService;
  }

  @GetMapping("/")
  public ResponseEntity<List<FuenteProxy>> getFuentesDeDatos() {
    return ResponseEntity.ok(fuenteProxyService.getFuentes());
  }

  @GetMapping("/{idFuenteDeDatos}")
  public FuenteProxy getFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return fuenteProxyService.obtenerFuente(idfuenteDeDatos);
  }

  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ResponseEntity<List<Hecho>> obtenerHechos(@PathVariable Integer idFuenteDeDatos) {
    //return serviceIncidencias.obtenerHechosXIDFuente(idfuenteDeDatos);
    return ResponseEntity.ok(fuenteProxyService.obtenerHechos(idFuenteDeDatos));
  }

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
}