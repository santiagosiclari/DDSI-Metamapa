package DTO;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Setter;

@Getter @Setter
@JsonInclude(Include.NON_NULL)
public class SolicitudEliminacionDTO {
  private  String motivo;
  private  String estado;
  private  String hechoAfectado;
  private  int id;
  public SolicitudEliminacionDTO() {
  }

  public SolicitudEliminacionDTO(SolicitudEliminacion solicitudEliminacion) {
    this.motivo = solicitudEliminacion.getMotivo();
    this.estado = solicitudEliminacion.getEstado().name();
    this.hechoAfectado = solicitudEliminacion.getHechoAfectado();
    this.id = solicitudEliminacion.getId();
  }
}