package Agregador.DTO;
import Agregador.business.Solicitudes.SolicitudEliminacion;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolicitudEliminacionDTO {
  private String motivo;
  private String estado;
  private String hechoAfectado;
  private int id;

  public SolicitudEliminacionDTO() {}

  public SolicitudEliminacionDTO(SolicitudEliminacion solicitudEliminacion) {
    this.motivo = solicitudEliminacion.getMotivo();
    this.estado = solicitudEliminacion.getEstado().name();
    this.hechoAfectado = solicitudEliminacion.getHechoAfectado();
    this.id = solicitudEliminacion.getId();
  }
}