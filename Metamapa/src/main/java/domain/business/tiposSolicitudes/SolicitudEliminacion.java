package domain.business.tiposSolicitudes;
import domain.business.incidencias.Hecho;

import lombok.Getter;


public class SolicitudEliminacion extends Solicitud{

  @Getter
  public String motivo;

  public SolicitudEliminacion(Hecho hechoAfectado, String motivo) {
    super(hechoAfectado, EstadoSolicitud.PENDIENTE); //por defecto se inicializan pendientes

    if (motivo == null || motivo.length() < 500) {
      throw new IllegalArgumentException("El motivo debe tener al menos 500 caracteres.");
    }

    /*if (DetectorDeSpam.esSpam(motivo)) {
      // Rechazo automático si es spam
      this.rechazarSolicitud();
      return;
    }*/

    this.motivo = motivo;
  }

  @Override
  public void aceptarSolicitud(){
    super.aceptarSolicitud();
    hechoAfectado.setEliminado(true);
  }
  public void rechazarSolicitud(){
    super.rechazarSolicitud();
  }
}