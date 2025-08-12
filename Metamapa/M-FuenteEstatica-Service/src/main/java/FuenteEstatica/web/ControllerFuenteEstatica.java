package FuenteEstatica.web;

import FuenteEstatica.persistencia.*;
import FuenteEstatica.business.FuentesDeDatos.*;
import FuenteEstatica.business.Hechos.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteEstatica {
  public RepositorioFuentes repositorioFuentes = new RepositorioFuentes();
  public RepositorioHechos repositorioHechos = new RepositorioHechos();

  public ControllerFuenteEstatica(){
  }

  @GetMapping("/")
  public ArrayList<FuenteEstatica> getFuenteDeDatos(){
    return repositorioFuentes.getFuentesDeDatos();
  }

  @GetMapping("/{idFuenteDeDatos}")
  public FuenteEstatica getFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }

  //TODO este me parece que no se usa, ya que el agregador se actualiza solo
  //TODO por ahi esta para otra cosa
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ArrayList<Hecho> getHechosFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return  repositorioHechos.getHechos().stream().filter(h -> h.getFuenteId() == idfuenteDeDatos).collect(Collectors.toCollection(ArrayList::new));
  }

  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> crearFuenteDeDatos(@RequestBody Map<String, Object> requestBody) {
    try {
      String nombreFE = (String) requestBody.get("nombre");
      FuenteEstatica fuenteEstatica = new FuenteEstatica(nombreFE);
      repositorioFuentes.agregarFuente(fuenteEstatica);
      return ResponseEntity.ok(fuenteEstatica);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }

//  @PostMapping (value = "/{idFuenteDeDatos}/cargarCSV", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
//  public ResponseEntity cargarCSV(
//      @PathVariable(value = "idFuenteDeDatos") Integer idFuenteDeDatos,
//      @RequestParam("file") MultipartFile file) {
//    try{
//      if (!repositorioFuentes.buscarFuente(idFuenteDeDatos).getTipoFuente().equals(TipoFuente.FUENTEESTATICA)) {
//        return ResponseEntity
//            .badRequest()
//            .body("Sólo se puede cargar CSV en fuentes estáticas");
//      }
//      //TODO repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream()).forEach(h -> repositorioHechos.agregar(h)); para tratar directamente con el repositorio de hechos en vez de con las fuentes
//      repositorioFuentes.buscarFuente(idFuenteDeDatos).agregarHecho(repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream(),idFuenteDeDatos));
//      return ResponseEntity.ok(repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream(),idFuenteDeDatos));
//
//
//
//    }catch (Exception e) {
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
//    }
//  }
}