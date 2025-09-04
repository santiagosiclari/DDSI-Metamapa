package Agregador.DTO;
import Agregador.business.Solicitudes.EstadoSolicitud;
import Agregador.business.Solicitudes.SolicitudEliminacion;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolicitudEliminacionDTO {
  private String motivo;
  private EstadoSolicitud estado;
  private Integer hechoAfectado;
  private int id;

  public SolicitudEliminacionDTO() {}

  public SolicitudEliminacionDTO(SolicitudEliminacion solicitudEliminacion) {
    this.motivo = solicitudEliminacion.getMotivo();
    this.estado = solicitudEliminacion.getEstado();
    this.hechoAfectado = solicitudEliminacion.getHechoAfectado();
    this.id = solicitudEliminacion.getId();
  }
}