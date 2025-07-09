package DTO;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import lombok.Getter;

@Getter
public class SolicitudEliminacionDTO {
  private final String motivo;
  private final String estado;
  private final String hechoAfectado;
  private final int id;
  public SolicitudEliminacionDTO(SolicitudEliminacion solicitudEliminacion) {
    this.motivo = solicitudEliminacion.getMotivo();
    this.estado = solicitudEliminacion.getEstado().name();
    this.hechoAfectado = solicitudEliminacion.getHechoAfectado();
    this.id = solicitudEliminacion.getId();
  }
}