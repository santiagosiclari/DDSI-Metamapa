package metemapaFuentes.web;
import DTO.HechoDTO;
import domain.business.FuentesDeDatos.*;
import domain.business.incidencias.Hecho;
import java.util.ArrayList;
import java.util.Map;
import metemapaFuentes.persistencia.RepositorioFuentes;
import metemapaFuentes.service.ServiceIncidencias;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api-fuentesDeDatos")

public class ControllerFuentesDeDatos {

  private final ServiceIncidencias serviceIncidencias;
  public RepositorioFuentes repositorioFuentes = new RepositorioFuentes();

  public ControllerFuentesDeDatos(ServiceIncidencias serviceIncidencias){
    this.serviceIncidencias= serviceIncidencias;
  }



  @GetMapping("/{idFuenteDeDatos}")
  public FuenteDeDatos getFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ArrayList<HechoDTO> getHechosFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return serviceIncidencias.obtenerHechosXIDFuente(idfuenteDeDatos);
  }

  @PostMapping (value = "/{idFuenteDeDatos}/cargarCSV", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
  public ResponseEntity cargarCSV(
      @PathVariable(value = "idFuenteDeDatos") Integer idFuenteDeDatos,
      @RequestParam("file") MultipartFile file) {
    try{
      if (!repositorioFuentes.buscarFuente(idFuenteDeDatos).getTipoFuente().equals(TipoFuente.FUENTEESTATICA)) {
        return ResponseEntity
            .badRequest()
            .body("Sólo se puede cargar CSV en fuentes estáticas");
      }
      //TODO repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream()).forEach(h -> repositorioHechos.agregar(h)); para tratar directamente con el repositorio de hechos en vez de con las fuentes
      repositorioFuentes.buscarFuente(idFuenteDeDatos).agregarHecho(repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream()));
      return ResponseEntity.ok(repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream()));



    }catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }

  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
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