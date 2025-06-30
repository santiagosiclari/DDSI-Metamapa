package controllers;
import domain.Persistencia.RepositorioSolicitudEliminacion;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import domain.business.tiposSolicitudes.EstadoSolicitud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import domain.business.incidencias.Hecho;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


@SpringBootApplication
@RestController
public class controllerSolicitudes {
  private RepositorioSolicitudEliminacion solicitudRepository = new RepositorioSolicitudEliminacion();

  public static void main(String[] args) {
    //SpringApplication.run(testApplication.class, args);
    SpringApplication app = new SpringApplication(controllers.controllerSolicitudes.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8082"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    context.close();
  }

  @GetMapping(value = "/solicitudesEliminacion", produces = "application/json")
  public ResponseEntity<List<SolicitudEliminacion>> obtenerTodasLasSolicitudes() {
    try {
      // Obtener todas las solicitudes del repositorio
      List<SolicitudEliminacion> solicitudes = solicitudRepository.findAll();
      if (solicitudes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // Si no hay solicitudes, devuelve un 204
      }
      // Devolver la lista de solicitudes
      return ResponseEntity.ok(solicitudes);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // En caso de error, devuelve un 500
    }
  }

  @PostMapping(value = "/solicitudesEliminacion", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity subirSolicitudEliminacion(@RequestBody Map<String, Object> requestBody) {
    try {
      // Extraemos el "motivo" del cuerpo de la solicitud
      String motivo = (String) requestBody.get("motivo");
      // Extraemos la información del "hechoAfectado"
      // TODO: EN VEZ DE PASAR EL HECHO CON SUS ATRIBUTOS, DEBERIA PASARSE EL ID PARA BUSCARLO EN BASE DE DATOS
      Map<String, Object> hechoData = (Map<String, Object>) requestBody.get("hechoAfectado");
      // Extraemos los datos del "hechoAfectado" y manejamos los valores opcionales (null)
      String titulo = (String) hechoData.get("titulo");
      String descripcion = (String) hechoData.get("descripcion");
      String categoria = (String) hechoData.get("categoria");
      // Validamos si latitud y longitud existen, si no, los dejamos como null
      Float latitud = hechoData.containsKey("latitud") ? ((Number) hechoData.get("latitud")).floatValue() : null;
      Float longitud = hechoData.containsKey("longitud") ? ((Number) hechoData.get("longitud")).floatValue() : null;
      // Validamos si fechaHecho existe, si no, dejamos null o podemos asignar un valor por defecto
      //LocalDate fechaHecho = hechoData.containsKey("fechaHecho") ? LocalDate.parse((String) hechoData.get("fechaHecho")) : null;
      // Crear el objeto Hecho, permitiendo valores null
      Hecho hechoAfectado = new Hecho(
              titulo,
              descripcion,
              categoria,
              latitud,
              longitud,
              null,
              null,
              false,
              null
      );

      // Crear la solicitud de eliminación
      SolicitudEliminacion solicitud = new SolicitudEliminacion(hechoAfectado, motivo);
      System.out.println("Solicitud de eliminacion creada: " + solicitud);
      solicitudRepository.save(solicitud);  // Asumiendo que el repositorio tiene un método save
      return ResponseEntity.ok(solicitud);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // Si hay un error, se responde con un error 500
    }
  }

  @PatchMapping(value = "/solicitudesEliminacion/{id}", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity actualizarEstadoSolicitud(@PathVariable("id") String id, @RequestBody Map<String, Object> requestBody) {
    try {
      Optional<SolicitudEliminacion> solicitudOpt = solicitudRepository.findById(id);
      if (solicitudOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
      SolicitudEliminacion solicitud = solicitudOpt.get();

      if (!requestBody.containsKey("estado")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Si no hay estado, devolver 400 Bad Request
      }
      String nuevoEstado = (String) requestBody.get("estado");

      try {
        EstadoSolicitud estado = EstadoSolicitud.valueOf(nuevoEstado);
        solicitud.setEstado(estado);
      } catch (IllegalArgumentException e) {
        // Si el valor no es válido en el enum, devolver un error 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado no válido.");
      }
      // Actualizar el estado de la solicitud
      solicitud.setEstado(EstadoSolicitud.valueOf(nuevoEstado));
      // Guardar los cambios en el repositorio
      solicitudRepository.save(solicitud);
      // Devolver la solicitud con el estado actualizado
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