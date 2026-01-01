package Agregador.business.Solicitudes;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
public class SolicitudEliminacion extends Solicitud {
  @Getter
  public String motivo;

  public SolicitudEliminacion(Hecho hechoAfectado, String motivo, EstadoSolicitud estado) {
    super(hechoAfectado, estado);
    this.motivo = motivo;
  }

  public SolicitudEliminacion() {}

  @Override
  public void aceptarSolicitud() {
    if (getEstado() != EstadoSolicitud.PENDIENTE)
      throw new IllegalStateException("Solo se puede aprobar si está pendiente");
    super.aceptarSolicitud();
    getHechoAfectado().setEliminado(true);
  }

  public void rechazarSolicitud() {
    super.rechazarSolicitud();
  }
}