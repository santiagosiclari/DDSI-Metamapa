package metemapaSolicitudes.web;

import metemapaSolicitudes.persistencia.RepositorioSolicitudEdicion;
import metemapaSolicitudes.persistencia.RepositorioSolicitudEliminacion;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import domain.business.tiposSolicitudes.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
@RestController
public class controllerSolicitudes {
  private final RepositorioSolicitudEliminacion solicitudEliminacionRepository = new RepositorioSolicitudEliminacion();
  private final RepositorioSolicitudEdicion solicitudEdicionRepository = new RepositorioSolicitudEdicion();

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(controllerSolicitudes.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8082"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    context.close();
  }

  @GetMapping(value = "/solicitudesEliminacion", produces = "application/json")
  public ResponseEntity<List<SolicitudEliminacion>> obtenerTodasLasSolicitudesEliminacion() {
    try {
      List<SolicitudEliminacion> solicitudes = solicitudEliminacionRepository.findAll();
      if (solicitudes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Si no hay solicitudes, devuelve un 204
      }
      return ResponseEntity.ok(solicitudes);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // En caso de error, devuelve un 500
    }
  }

  @PostMapping(value = "/solicitudesEliminacion", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity subirSolicitudEliminacion(@RequestBody Map<String, Object> requestBody) {
    try {
      String motivo = (String) requestBody.get("motivo");
      String hecho = (String) requestBody.get("id");
      // Crear la solicitud de eliminación
      SolicitudEliminacion solicitud = new SolicitudEliminacion(hecho, motivo);
      System.out.println("Solicitud de eliminacion creada: " + solicitud);
      solicitudEliminacionRepository.save(solicitud);
      return ResponseEntity.ok(solicitud);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  private void responderSolicitud(Solicitud solicitud, Map<String, Object> requestBody) {
    try {
      String nuevoEstado = (String) requestBody.get("estado");

      solicitud.setEstado(EstadoSolicitud.valueOf(nuevoEstado));
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PatchMapping(value = "/solicitudesElimincacion/{id}", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity actualizarEstadoSolicitudEliminacion(@PathVariable("id") String id, @RequestBody Map<String, Object> requestBody) {
    try {
      Optional<SolicitudEliminacion> solicitudOpt = solicitudEliminacionRepository.findById(id);
      if (solicitudOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      SolicitudEliminacion solicitud = solicitudOpt.get();
      responderSolicitud(solicitud, requestBody);
      solicitudEliminacionRepository.save(solicitud);

      // TODO tirar al servicio de incidencias que se elimine

      return ResponseEntity.ok(solicitud);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @GetMapping(value = "/solicitudesEdicion", produces = "application/json")
  @ResponseBody
  public ResponseEntity<List<SolicitudEdicion>> obtenerTodasLasSolicitudesEdicion() {
    try {
      List<SolicitudEdicion> solicitudes = solicitudEdicionRepository.findAll();

      if (solicitudes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Si no hay solicitudes, devuelve un 204
      }

      return ResponseEntity.ok(solicitudes);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // En caso de error, devuelve un 500
    }
  }

  @PostMapping(value = "/solicitudesEdicion", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity subirSolicitudEdicion(@RequestBody Map<String, Object> requestBody) {
    try {
      String motivo = (String) requestBody.get("motivo");
      String hecho = (String) requestBody.get("id");
      // Crear la solicitud de eliminación
      //TODO cambiar las solicitudes de edicion para que usen un JSON en ves de todos los atributos por separado
      SolicitudEdicion solicitud = new SolicitudEdicion(hecho,(String)requestBody.get("hechoModficicado") ,motivo);
      System.out.println("Solicitud de eliminacion creada: " + solicitud);

      solicitudEdicionRepository.save(solicitud);
      return ResponseEntity.ok(solicitud);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping(value = "/solicitudesEdicion/{id}", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity actualizarEstadoSolicitudEdicion(@PathVariable("id") String id, @RequestBody Map<String, Object> requestBody) {
    try {
      Optional<SolicitudEdicion> solicitudOpt = solicitudEdicionRepository.findById(id);
      if (solicitudOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }

      SolicitudEdicion solicitud = solicitudOpt.get();
      responderSolicitud(solicitud, requestBody);

      solicitudEdicionRepository.save(solicitud);

      //TODO decirle al servicion de incidentes que se edite


      return ResponseEntity.ok(solicitud);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}



//@PostMapping(value = "/responderSolicitud/{solicitud}")



/*class SolicitudEliminacionDTO {
  private String motivo;
  private String estado;
  private Hecho hechoAfectado;
  public SolicitudEliminacionDTO(SolicitudEliminacion solicitudEliminacion) {
    this.motivo = solicitudEliminacion.getMotivo();
    this.estado = solicitudEliminacion.getEstado().name();
    this.hechoAfectado = solicitudEliminacion.getHecho();
  }
}*/