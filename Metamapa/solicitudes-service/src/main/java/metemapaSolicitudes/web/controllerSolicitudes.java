package metemapaSolicitudes.web;
import domain.business.incidencias.Hecho;
import jakarta.validation.Valid;
import metemapaSolicitudes.Service.ServiceIncidencia;
import DTO.SolicitudEdicionDTO;
import DTO.SolicitudEliminacionDTO;
import metemapaSolicitudes.persistencia.RepositorioSolicitudEdicion;
import metemapaSolicitudes.persistencia.RepositorioSolicitudEliminacion;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import domain.business.tiposSolicitudes.*;
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
  private final ServiceIncidencia serviceIncidencia;

  public controllerSolicitudes(ServiceIncidencia serviceIncidencia){
    this.serviceIncidencia = serviceIncidencia;
  }

  @GetMapping(value = "/solicitudesEliminacion", produces = "application/json")
  public ResponseEntity<List<SolicitudEliminacionDTO>> obtenerTodasLasSolicitudesEliminacion() {
    try {
      List<SolicitudEliminacionDTO> solicitudes = solicitudEliminacionRepository.findAll().stream()
              .map(SolicitudEliminacionDTO::new)
              .collect(Collectors.toList());
      if (solicitudes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Si no hay solicitudes, devuelve un 204
      }
      return ResponseEntity.ok(solicitudes);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // En caso de error, devuelve un 500
    }
  }

  @GetMapping("/solicitudesEliminacion/{id}")
  public ResponseEntity<SolicitudEliminacionDTO> obtenerSolicitudEliminacionPorId(@PathVariable("id") Integer id) {
    try {
      Optional<SolicitudEliminacion> coleccionOpt = solicitudEliminacionRepository.findById(id);
      return coleccionOpt.map(coleccion -> ResponseEntity.ok(new SolicitudEliminacionDTO(coleccion)))
              .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      System.err.println("Error al obtener colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping(value = "/solicitudesEliminacion", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SolicitudEliminacionDTO> subirSolicitudEliminacion(
      @RequestBody @Valid SolicitudEliminacionDTO solicitudRequestDTO) {
    try {
      String motivo = solicitudRequestDTO.getMotivo();
      String hechoAfectado = solicitudRequestDTO.getHechoAfectado();
      SolicitudEliminacion solicitud = new SolicitudEliminacion(hechoAfectado, motivo);
      solicitudEliminacionRepository.save(solicitud);
      return ResponseEntity.status(HttpStatus.CREATED).body(new SolicitudEliminacionDTO(solicitud));
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private void responderSolicitud(Solicitud solicitud, Map<String, Object> requestBody) {
    try {
      String nuevoEstado = (String) requestBody.get("estado");
      solicitud.setEstado(EstadoSolicitud.valueOf(nuevoEstado));
      Hecho hecho = (Hecho) requestBody.get("hechoAfectado");
      if(nuevoEstado.equals("APROBADO")){
        hecho.setEliminado(true); //Marca hecho como eliminado
        //TODO: no mostrar mas en colecciones una vez que el hecho se "elimina"
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PatchMapping(value = "/solicitudesElimincacion/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SolicitudEliminacionDTO> actualizarEstadoSolicitudEliminacion(@PathVariable("id") Integer id, @RequestBody Map<String, Object> requestBody) {
    try {
      Optional<SolicitudEliminacion> solicitudOpt = solicitudEliminacionRepository.findById(id);
      if (solicitudOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      SolicitudEliminacion solicitud = solicitudOpt.get();
      responderSolicitud(solicitud, requestBody);
      solicitudEliminacionRepository.save(solicitud);
      return ResponseEntity.ok(new SolicitudEliminacionDTO(solicitud));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @GetMapping(value = "/solicitudesEdicion", produces = "application/json")
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
  public ResponseEntity subirSolicitudEdicion(@RequestBody SolicitudEdicionDTO solicitudEdicionDTO) {
    try {
      SolicitudEdicion solicitud = new SolicitudEdicion(
          solicitudEdicionDTO.getTituloMod(),
          solicitudEdicionDTO.getDescMod(),
          solicitudEdicionDTO.getCategoriaMod(),
          solicitudEdicionDTO.getUbicacionMod(),
          solicitudEdicionDTO.getFechaHechoMod(),
          solicitudEdicionDTO.getMultimediaMod(),
          solicitudEdicionDTO.getAnonimidadMod(),
          solicitudEdicionDTO.getSugerencia(),
          solicitudEdicionDTO.getHechoAfectado()
      );
      System.out.println("Solicitud de edicion creada: " + solicitud);
      solicitudEdicionRepository.save(solicitud);
      return ResponseEntity.ok(solicitud);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping(value = "/solicitudesEdicion/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity actualizarEstadoSolicitudEdicion(@PathVariable("id") Integer id, @RequestBody Map<String, Object> requestBody) {
    try {
      Optional<SolicitudEdicion> solicitudOpt = solicitudEdicionRepository.findById(id);
      if (solicitudOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      SolicitudEdicion solicitud = solicitudOpt.get();
      responderSolicitud(solicitud, requestBody);
      solicitudEdicionRepository.save(solicitud);

      //Decirle al servicion de incidentes que se edite
      if(solicitud.getEstado() == EstadoSolicitud.APROBADA){

        serviceIncidencia.aplicarEdicionIncidencia(solicitud);
        //serviceIncidencia.aplicarEdicionIncidencia(solicitud.getHechoAfectado().getId(), requestBody);
      }
      return ResponseEntity.ok(solicitud);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}