package Agregador.web;
import Agregador.DTO.*;
import Agregador.Service.ServiceSolicitudes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class ControllerSolicitudes {
    private final ServiceSolicitudes service;

    // Obtiene todas las solicitudes de eliminación, opcional que sean spam, para reutilizar si hace falta
    @GetMapping(value = "/solicitudesEliminacion", produces = "application/json")
    public ResponseEntity<List<SolicitudEliminacionDTO>> obtenerTodasLasSolicitudesEliminacion(@RequestParam(required = false) Boolean spam) {
        List<SolicitudEliminacionDTO> solicitudes = service.obtenerTodasSolicitudesEliminacion(spam);
        return ResponseEntity.ok(solicitudes);
    }

    enum Accion {APROBAR, RECHAZAR}

    @PatchMapping("/solicitudesEliminacion/{id}")
    public ResponseEntity<Void> actualizarEstadoSolicitud(@PathVariable Integer id, @RequestBody AccionSolicitudDTO dto) {
        Accion a = Accion.valueOf(dto.getAccion().trim().toUpperCase());
        var r = (a == Accion.APROBAR) ? service.aprobar(id) : service.rechazar(id);
        return switch (r) {
            case OK -> ResponseEntity.noContent().build();
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case CONFLICT -> ResponseEntity.status(409).build();
            default -> ResponseEntity.unprocessableEntity().build();
        };
    }

    //Crea una solicitud de eliminacion
    @PostMapping(value = "/solicitudesEliminacion", consumes = "application/json", produces = "application/json")
    public ResponseEntity<SolicitudEliminacionDTO> subirSolicitudEliminacion(@RequestBody SolicitudEliminacionDTO solicitudEliminacionDTO) {
        SolicitudEliminacionDTO respuesta = service.crearSolicitud(solicitudEliminacionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping(value = "/solicitudesEliminacion/{id}", produces = "application/json")
    public ResponseEntity<SolicitudEliminacionDTO> obtenerSolicitud(@PathVariable Integer id) {
        SolicitudEliminacionDTO solicitudDTO = service.buscarPorId(id);
        return ResponseEntity.ok(solicitudDTO);
    }


    //Obtiene todas las solicitudes de edicion
    @GetMapping(value = "/solicitudesEdicion", produces = "application/json")
    public ResponseEntity<List<SolicitudEdicionDTO>> obtenerTodasLasSolicitudesEdicion() {
        List<SolicitudEdicionDTO> solicitudes = service.obtenerTodasSolicitudesEdicion();
        return ResponseEntity.ok(solicitudes);
    }

    //Obtiene una solicitud de edicion por ID
    @GetMapping(value = "/solicitudesEdicion/{id}", produces = "application/json")
    public ResponseEntity<SolicitudEdicionDTO> obtenerSolicitudEdicionPorId(@PathVariable Integer id) {
        SolicitudEdicionDTO solicitudDTO = service.obtenerSolicitudEdicionPorId(id);
        return ResponseEntity.ok(solicitudDTO);
    }

    //Crea una solicitud de edicion
    @PostMapping(value = "/solicitudesEdicion", consumes = "application/json", produces = "application/json")
    public ResponseEntity<SolicitudEdicionDTO> subirSolicitudEdicion(@RequestBody SolicitudEdicionDTO solicitudEdicionDTO) {
        SolicitudEdicionDTO respuestaDTO = service.crearSolicitudEdicion(solicitudEdicionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaDTO);
    }

    //Cambia el estado de una solicitud de edicion
    @PatchMapping(value = "/solicitudesEdicion/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<SolicitudEdicionDTO> actualizarEstadoSolicitudEdicion(@PathVariable Integer id, @RequestBody Map<String, Object> requestBody) {
        SolicitudEdicionDTO respuesta = service.actualizarEstadoSolicitudEdicion(id, requestBody);
        return ResponseEntity.ok(respuesta);
    }
}