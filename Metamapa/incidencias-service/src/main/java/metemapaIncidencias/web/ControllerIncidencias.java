package metemapaIncidencias.web;
import DTO.HechoDTO;
import domain.business.Usuarios.Perfil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import metemapaIncidencias.persistencia.RepositorioHechos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import domain.business.incidencias.*;

@SpringBootApplication
@RestController
public class ControllerIncidencias {
  RepositorioHechos repositorio = new RepositorioHechos();

  public static void main(String[] args) {
    //SpringApplication.run(testApplication.class, args);
    SpringApplication app = new SpringApplication(metemapaIncidencias.web.ControllerIncidencias.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    context.close();
  }

  @GetMapping(value = "/hechos", produces = "application/json")
  public ResponseEntity<ArrayList<HechoDTO>> obtenerTodosLosHechos() {
    try {
      ArrayList<HechoDTO> hechos = repositorio.getHechos().stream()
          .map(HechoDTO::new)
          .collect(Collectors.toCollection(ArrayList::new));
      if (hechos.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Si no hay hechos, devuelve un 204
      }
      return ResponseEntity.ok(hechos);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // En caso de error, devuelve un 500
    }
  }

  private Hecho jsonToHechos(Map<String, Object> json) {
    Hecho hecho = new Hecho((String)json.get("titulo"),(String)json.get("descripcion"),(String)json.get("categoria"),(Float)json.get("latitud"),(Float)json.get("longitud"),(LocalDate)json.get("fechaHecho"),(Perfil)json.get("autor"),(Integer)json.get("fuenteId"),(Boolean)json.get("anonimo"),(List<Multimedia>)json.get("multimedia"));
    return hecho;
  }

  private HechoDTO jsonToHechoDTO(Map<String, Object> json) {
    return new HechoDTO(jsonToHechos(json));
  }
  @PostMapping(value = "/hechos", consumes = "aplication/json",produces = "application/json")
  public ResponseEntity<Hecho> publicarHecho(@RequestBody Map<String, Object> requestBody){
    Hecho hecho = jsonToHechos(requestBody);
    try {

      repositorio.agregarHecho(hecho);
    }
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    return ResponseEntity.ok(hecho);
  }

  @GetMapping (value = "/hechos/{idFuenteDeDatos}",produces = "application/json")
  public ResponseEntity <ArrayList<HechoDTO>> obtenerHechosXIDFuente(@PathVariable("idFuenteDeDatos") Integer idFuenteDeDatos){
    try{
      ArrayList<HechoDTO> hechos =  repositorio.getHechos().stream().filter(h-> h.getFuenteId() == idFuenteDeDatos).map(HechoDTO::new).collect(Collectors.toCollection(ArrayList::new));
      if (hechos.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Si no hay hechos, devuelve un 204
    }
      return ResponseEntity.ok(hechos);
    }catch (Exception e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

@PatchMapping(value = "/hechos/{idHecho}",consumes = "application/json")
  public ResponseEntity<HechoDTO> modificarHecho(@RequestBody Map<String, Object> requestBody,@PathVariable("idHecho") Integer idHecho)
  {
      try
      {
          Hecho hechoOriginal = repositorio.findHecho(idHecho);
          hechoOriginal.editarHecho((String)requestBody.get("titulo"),
                                    (String)requestBody.get("descripcion"),
                                    (String)requestBody.get("categoria"),
                                    (Float)requestBody.get("latitud"),
                                    (Float)requestBody.get("longitud"),
                                    (LocalDate)requestBody.get("fechaHecho"),
                                    (Boolean)requestBody.get("anonimo"),
                                    (ArrayList<Multimedia>)requestBody.get("multimedia"));
          repositorio.modificarHecho(hechoOriginal);
          return ResponseEntity.ok(jsonToHechoDTO(requestBody));
      }
      catch (Exception e)
      {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
      }
  }


  @DeleteMapping(value = "hechos/{idHecho}", consumes = "application/json")
  public ResponseEntity<HechoDTO> eliminarHecho(@RequestBody Map<String, Object> requestBody,@PathVariable("idHecho") Integer idHecho)
  {
    try
    {
      Hecho hecho = repositorio.findHecho(idHecho);
      hecho.setEliminado(true);

      repositorio.modificarHecho(hecho);
      return ResponseEntity.ok(jsonToHechoDTO(requestBody));
    }
    catch (Exception e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
  //@GetMapping(value = )
}
