package metemapaFuentes.web;


import domain.business.FuentesDeDatos.FuenteDeDatos;
import domain.business.FuentesDeDatos.FuenteDemo;
import domain.business.FuentesDeDatos.FuenteEstatica;
import domain.business.FuentesDeDatos.FuenteMetamapa;
import java.util.Map;
import metemapaFuentes.persistencia.RepositorioFuentes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-fuentesDeDatos")

public class ControllerFuentesDeDatos {

  public RepositorioFuentes repositorioFuentes = new RepositorioFuentes();

  @GetMapping("/{idFuenteDeDatos}")
  public FuenteDeDatos getFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }

  @PostMapping(value = "/crear", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity crearFuenteDeDatos(@RequestBody Map<String, Object> requestBody) {
    try {
      switch (requestBody.get("tipo").toString()) {
        case "FuenteEstatica":
          String nombreFE = (String) requestBody.get("nombre");
          FuenteEstatica fuenteEstatica = new FuenteEstatica(nombreFE);
          repositorioFuentes.agregarFuente(fuenteEstatica);
          return ResponseEntity.ok(fuenteEstatica);
        case "FuenteDemo":
          String nombreFD = (String) requestBody.get("nombre");
          String urlFD = (String) requestBody.get("url");
          FuenteDemo fuenteDemo = new FuenteDemo(nombreFD, urlFD);
          repositorioFuentes.agregarFuente(fuenteDemo);
          return ResponseEntity.ok(fuenteDemo);
        case "FuenteMetamapa":
          String nombreFM = (String) requestBody.get("nombre");
          String urlFM = (String) requestBody.get("url");
          FuenteMetamapa fuenteMetamapa = new FuenteMetamapa(nombreFM, urlFM);
          repositorioFuentes.agregarFuente(fuenteMetamapa);
          return ResponseEntity.ok(fuenteMetamapa);
        default:
          // tipo no reconocido -> 400 Bad Request
          return ResponseEntity
              .badRequest()
              .body("Tipo de fuente inválido: " + requestBody.get("tipo").toString());
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }
}