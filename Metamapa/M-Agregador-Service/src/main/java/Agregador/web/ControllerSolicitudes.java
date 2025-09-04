package Agregador.web;
import Agregador.DTO.AccionSolicitudDTO;
import Agregador.DTO.SolicitudEliminacionDTO;
import Agregador.Service.ServiceSolicitudes;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-solicitudes")
public class ControllerSolicitudes {
    private final ServiceSolicitudes service;

    public ControllerSolicitudes(ServiceSolicitudes service) { this.service = service; }

    enum Accion { APROBAR, RECHAZAR }

    @PatchMapping("/solicitudesEliminacion/{id}")
    public ResponseEntity<Void> actualizarEstadoSolicitud(@PathVariable Integer id,
                                                          @RequestBody AccionSolicitudDTO dto) {
        Accion a;
        try {
            a = Accion.valueOf(dto.getAccion().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.unprocessableEntity().build(); // 422 parámetro inválido
        }

        var r = (a == Accion.APROBAR) ? service.aprobar(id) : service.rechazar(id);
        return switch (r) {
            case OK        -> ResponseEntity.noContent().build();
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case CONFLICT  -> ResponseEntity.status(409).build();
            default        -> ResponseEntity.unprocessableEntity().build();
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

    @GetMapping(value = "/solicitudesEliminacion/{id}", produces = "application/json")
    public ResponseEntity<?> obtenerSolicitud(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
