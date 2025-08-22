package FuenteDinamica.web;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import FuenteDinamica.persistencia.RepositorioFuentes;
import FuenteDinamica.business.Hechos.*;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public FuenteDinamica getFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }

  //TODO este me parece que no se usa, ya que el agregador se actualiza solo
  //TODO por ahi esta para otra cosa
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
  public ResponseEntity<?> cargarHecho(@PathVariable(value = "idFuenteDeDatos") Integer idFuenteDeDatos, @RequestBody Map<String, Object> requestBody) {
    try{
      /*if (!repositorioFuentes.buscarFuente(idFuenteDeDatos)) {
        return ResponseEntity
            .badRequest()
            .body(Map.of("error", "Sólo se puede cargar un hecho manual en fuentes dinámicas"));
      }*/
      String titulo = (String) requestBody.get("titulo");
      String descripcion = (String) requestBody.get("descripcion");
      String categoria = (String) requestBody.get("categoria");
      //Float latitud = (Float) requestBody.get("latitud");
      //Float longitud = (Float) requestBody.get("longitud");
      //LocalDate fechaHecho = (LocalDate) requestBody.get("fechaHecho");
      Number latNum = (Number) requestBody.get("latitud");
      Float latitud = latNum != null ? latNum.floatValue() : null;
      Number lonNum = (Number) requestBody.get("longitud");
      Float longitud = lonNum != null ? lonNum.floatValue() : null;
      String fechaStr = (String) requestBody.get("fechaHecho");
      LocalDate fechaHecho = (fechaStr != null && !fechaStr.isBlank())
          ? LocalDate.parse(fechaStr)
          : null;
      Integer autor = (Integer) requestBody.get("idUsuario");
      //Perfil autor = null;
      //Perfil autor = (Perfil) requestBody.get("autor");
      //Boolean anonimo = (Boolean) requestBody.get("anonimo");
      //ArrayList<Multimedia> multimedia = null;
      //ArrayList<Multimedia> multimedia = (ArrayList<Multimedia>) requestBody.get("multimedia");
      Boolean anonimo = false;
      if (requestBody.get("anonimo") instanceof Boolean) {
        anonimo = (Boolean) requestBody.get("anonimo");
      }
      @SuppressWarnings("unchecked")
      List<Map<String,Object>> mmMaps = (List<Map<String,Object>>) requestBody.get("multimedia");
      List<Multimedia> multimedia = mmMaps != null
          ? mmMaps.stream().map(m -> {
        // extraigo tipo y ruta
        String tipoStr = (String) m.get("tipoMultimedia");
        String path    = (String) m.get("path");
        // convierto la cadena al enum
        TipoMultimedia tipo = TipoMultimedia.valueOf(tipoStr);
        return new Multimedia(tipo, path);
      }).collect(Collectors.toList())
          : new ArrayList<>();
      Hecho hecho = new Hecho(
              titulo,
              descripcion,
              categoria,
              latitud,
              longitud,
              fechaHecho,
              autor,
              idFuenteDeDatos,
              anonimo,
              multimedia);
      repositorioFuentes.buscarFuente(idFuenteDeDatos).getHechos().add(hecho);
      return ResponseEntity.ok(hecho);
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error interno: " + e.getMessage()));
    }
  }
}