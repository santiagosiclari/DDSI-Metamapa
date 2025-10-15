package Agregador.DTO;
import Agregador.business.Solicitudes.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigInteger;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolicitudEliminacionDTO {
  private String motivo;
  private EstadoSolicitud estado;
  private BigInteger hechoAfectado;
  private Integer id;

  public SolicitudEliminacionDTO() {}

  public SolicitudEliminacionDTO(SolicitudEliminacion solicitudEliminacion) {
    this.motivo = solicitudEliminacion.getMotivo();
    this.estado = solicitudEliminacion.getEstado();
    this.hechoAfectado = solicitudEliminacion.getHechoAfectado().getId();
    this.id = solicitudEliminacion.getId();
  }
}