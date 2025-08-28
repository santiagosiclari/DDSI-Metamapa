package FuenteDinamica.web;
import java.util.*;
import FuenteDinamica.business.DTO.HechoDTO;
import FuenteDinamica.persistencia.RepositorioFuentes;
import FuenteDinamica.business.Hechos.*;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteDinamica {
  public RepositorioFuentes repositorioFuentes = new RepositorioFuentes();

  /*public ControllerFuenteDinamica(ServiceIncidencias serviceIncidencias){
    this.serviceIncidencias= serviceIncidencias;
  }*/

  @GetMapping("/")
  public ArrayList<FuenteDinamica> getFuenteDeDatos(){
    return repositorioFuentes.getFuentesDinamicas();
  }

  @GetMapping("/{idFuenteDeDatos}")
  public FuenteDinamica getFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }

  //TODO este me parece que no se usa, ya que el agregador se actualiza solo
  // por ahi esta para otra cosa
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ArrayList<Hecho> getHechosFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos).getHechos();
  }

  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> crearFuenteDeDatos(@RequestBody Map<String, Object> requestBody) {
    try {
      //String nombreFE = (String) requestBody.get("nombre");
      FuenteDinamica fuenteDinamica = new FuenteDinamica();
      repositorioFuentes.agregarFuente(fuenteDinamica);
      return ResponseEntity.ok(fuenteDinamica);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }

  @PostMapping (value = "/{idFuenteDeDatos}/cargarHecho", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> cargarHecho(@PathVariable Integer idFuenteDeDatos, @Valid @RequestBody HechoDTO hechoDTO) {
    try {
      Hecho hecho = hechoDTO.toDomain(idFuenteDeDatos);
      repositorioFuentes.buscarFuente(idFuenteDeDatos).getHechos().add(hecho);
      return ResponseEntity.ok(hecho);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno: " + e.getMessage()));
    }
  }
}