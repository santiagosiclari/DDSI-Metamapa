package metemapaIncidencias.web;
import domain.business.Usuarios.Perfil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import metemapaIncidencias.persistencia.HechosRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import domain.business.incidencias.*;

@SpringBootApplication
@RestController
public class ControllerIncidencias {
  HechosRepository repositorio = new HechosRepository();

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
  public ResponseEntity<List<HechoDTO>> obtenerTodosLosHechos() {
    try {
      List<HechoDTO> hechos = repositorio.findAll().stream()
          .map(HechoDTO::new)
          .collect(Collectors.toList());
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

  @PostMapping(value = "/hechos", consumes = "aplication/json",produces = "application/json")
  public ResponseEntity<Hecho> publicarHecho(@RequestBody Map<String, Object> requestBody){
    Hecho hecho = jsonToHechos(requestBody);
    try {

      repositorio.save(hecho);
    }
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    return ResponseEntity.ok(hecho);
  }

  //@GetMapping(value = )


  @Getter
  static public class HechoDTO {
    private String titulo;
    private String descripcion;
    private String categoria;
    private Ubicacion ubicacion;
    private LocalDate fechaHecho;
    private LocalDate fechaCarga;
    private LocalDate fechaModificacion;
    private Perfil perfil;
    private int fuenteId;
    private Boolean anonimo;
    private Boolean eliminado;
    private List<Multimedia> multimedia;
    private HashMap<String, String> metadata;
    private int id;
    public HechoDTO(Hecho hecho) {
      this.titulo = hecho.getTitulo();
      this.descripcion = hecho.getDescripcion();
      this.categoria = hecho.getCategoria();
      this.ubicacion = hecho.getUbicacion();
      this.fechaHecho = hecho.getFechaHecho();
      this.fechaCarga = hecho.getFechaCarga();
      this.fechaModificacion = hecho.getFechaModificacion();
      this.perfil = hecho.getPerfil();
      this.fuenteId = hecho.getFuenteId();
      this.anonimo = hecho.getAnonimo();
      this.eliminado = hecho.getEliminado();
      this.multimedia = hecho.getMultimedia();
      this.metadata = hecho.getMetadata();
      this.id = hecho.getId();
    }
  }
}
