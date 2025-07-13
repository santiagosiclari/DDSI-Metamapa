package metemapaSolicitudes.web;
import java.util.NoSuchElementException;
import metemapaSolicitudes.Service.ServiceIncidencia;
import DTO.SolicitudEdicionDTO;
import DTO.SolicitudEliminacionDTO;
import metemapaSolicitudes.Service.ServiceSolicitudEdicion;
import metemapaSolicitudes.Service.ServiceSolicitudEliminacion;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import domain.business.tiposSolicitudes.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
@RestController
public class controllerSolicitudes {
  private final ServiceIncidencia serviceIncidencia;
  private final ServiceSolicitudEliminacion serviceSolicitudEliminacion;
  private final ServiceSolicitudEdicion serviceSolicitudEdicion;
  public controllerSolicitudes(ServiceSolicitudEliminacion serviceSolicitudEliminacion, ServiceSolicitudEdicion serviceSolicitudEdicion, ServiceIncidencia serviceIncidencia) {
    this.serviceSolicitudEliminacion = serviceSolicitudEliminacion;
    this.serviceSolicitudEdicion = serviceSolicitudEdicion;
    this.serviceIncidencia = serviceIncidencia;
  }

  //Obtiene todas las solicitudes de eliminacion
  @GetMapping(value = "/solicitudesEliminacion", produces = "application/json")
  public ResponseEntity<List<SolicitudEliminacionDTO>> obtenerTodasLasSolicitudesEliminacion() {
    try {
      List<SolicitudEliminacionDTO> solicitudes = serviceSolicitudEliminacion.obtenerTodasSolicitudesEliminacion();
      if (solicitudes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Si no hay solicitudes, devuelve un 204
      }
      return ResponseEntity.ok(solicitudes);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // En caso de error, devuelve un 500
    }
  }

  //Obtiene una solicitud de eliminacion por ID
  @GetMapping("/solicitudesEliminacion/{id}")
  public ResponseEntity<SolicitudEliminacionDTO> obtenerSolicitudEliminacionPorId(@PathVariable("id") Integer id) {
    try {
      Optional<SolicitudEliminacionDTO> dtoOpt = serviceSolicitudEliminacion.obtenerPorId(id);
      return dtoOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      System.err.println("Error al obtener colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  //Crea una solicitud de eliminacion
  @PostMapping(value = "/solicitudesEliminacion", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> subirSolicitudEliminacion(@RequestBody SolicitudEliminacionDTO solicitudEliminacionDTO) {
    try {
      SolicitudEliminacionDTO respuesta = serviceSolicitudEliminacion.crearSolicitud(solicitudEliminacionDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El motivo no es válido");
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  //Cambia el estado de una solicitud de eliminacion
  @PatchMapping(value = "/solicitudesEliminacion/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SolicitudEliminacionDTO> actualizarEstadoSolicitudEliminacion(
      @PathVariable("id") Integer id,
      @RequestBody Map<String, Object> requestBody) {
    try {
      SolicitudEliminacionDTO respuesta = serviceSolicitudEliminacion.actualizarEstado(id, requestBody);
      return ResponseEntity.ok(respuesta);
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      System.err.println("Error al actualizar estado: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  //Obtiene todas las solicitudes de edicion
  @GetMapping(value = "/solicitudesEdicion", produces = "application/json")
  public ResponseEntity<List<SolicitudEdicion>> obtenerTodasLasSolicitudesEdicion() {
    try {
      List<SolicitudEdicion> solicitudes = serviceSolicitudEdicion.obtenerTodas();
      if (solicitudes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Si no hay solicitudes, devuelve un 204
      }
      return ResponseEntity.ok(solicitudes);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // En caso de error, devuelve un 500
    }
  }

  //Obtiene una solicitud de edicion por ID
  @GetMapping(value = "/solicitudesEdicion/{id}", produces = "application/json")
  public ResponseEntity<SolicitudEdicionDTO> obtenerSolicitudEdicionPorId(@PathVariable("id") Integer id) {
    try {
      SolicitudEdicionDTO solicitudDTO = serviceSolicitudEdicion.obtenerSolicitudEdicionPorId(id);
      if (solicitudDTO == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      return ResponseEntity.ok(solicitudDTO);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  //Crea una solicitud de edicion
  @PostMapping(value = "/solicitudesEdicion", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SolicitudEdicionDTO> subirSolicitudEdicion(@RequestBody SolicitudEdicionDTO solicitudEdicionDTO) {
    try {
      //TODO: antes de crear, llamar al service de indicencias con el id del hecho para obtener su fecha y verificar plazo maximo de una semana
      SolicitudEdicionDTO respuestaDTO = serviceSolicitudEdicion.crearSolicitudEdicion(solicitudEdicionDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(respuestaDTO);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  //Cambia el estado de una solicitud de edicion
  @PatchMapping(value = "/solicitudesEdicion/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> actualizarEstadoSolicitudEdicion(@PathVariable("id") Integer id, @RequestBody Map<String, Object> requestBody) {
    try {
      SolicitudEdicionDTO respuesta = serviceSolicitudEdicion.actualizarEstadoSolicitudEdicion(id, requestBody);
      if (respuesta == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      return ResponseEntity.ok(respuesta);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}