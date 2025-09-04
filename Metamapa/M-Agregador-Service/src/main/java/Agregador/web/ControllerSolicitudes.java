package Agregador.web;
import Agregador.DTO.SolicitudEdicionDTO;
import Agregador.DTO.SolicitudEliminacionDTO;
import Agregador.Service.ServiceSolicitudes;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api-agregador")
public class ControllerSolicitudes {
    private final ServiceSolicitudes service;

    public ControllerSolicitudes(ServiceSolicitudes service) { this.service = service; }

    enum Accion { APROBAR, RECHAZAR }

    @PatchMapping("/solicitudes/{id}")
    public ResponseEntity<Void> actualizarEstadoSolicitud(@PathVariable Integer id,
                                                          @RequestParam Accion accion) {
        var r = (accion == Accion.APROBAR) ? service.aprobar(id) : service.rechazar(id);
        return switch (r) {
            case OK        -> ResponseEntity.noContent().build();   // 204
            case NOT_FOUND -> ResponseEntity.notFound().build();    // 404
            case CONFLICT  -> ResponseEntity.status(409).build();   // ya resuelta
            default        -> ResponseEntity.unprocessableEntity().build(); // 422
        };
    }

    //Crea una solicitud de eliminacion
    @PostMapping(value = "/solicitudesEliminacion", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> subirSolicitudEliminacion(@RequestBody SolicitudEliminacionDTO solicitudEliminacionDTO) {
        try {
            SolicitudEliminacionDTO respuesta = service.crearSolicitud(solicitudEliminacionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El motivo no es válido");
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //Obtiene todas las solicitudes de edicion
    @GetMapping(value = "/solicitudesEdicion", produces = "application/json")
    public ResponseEntity<List<SolicitudEdicionDTO>> obtenerTodasLasSolicitudesEdicion() {
        try {
            List<SolicitudEdicionDTO> solicitudes = service.obtenerTodasSolicitudesEdicion();
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
            SolicitudEdicionDTO solicitudDTO = service.obtenerSolicitudEdicionPorId(id);
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
            SolicitudEdicionDTO respuestaDTO = service.crearSolicitudEdicion(solicitudEdicionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuestaDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Cambia el estado de una solicitud de edicion
    @PatchMapping(value = "/solicitudesEdicion/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> actualizarEstadoSolicitudEdicion(@PathVariable("id") Integer id, @RequestBody Map<String, Object> requestBody) {
        try {
            SolicitudEdicionDTO respuesta = service.actualizarEstadoSolicitudEdicion(id, requestBody);
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