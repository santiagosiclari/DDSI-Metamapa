package Agregador.web;

import Agregador.DTO.SolicitudEliminacionDTO;
import Agregador.Service.ServiceSolicitudes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
