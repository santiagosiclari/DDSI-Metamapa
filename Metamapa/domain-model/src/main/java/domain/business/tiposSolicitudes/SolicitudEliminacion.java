package domain.business.tiposSolicitudes;
import domain.business.incidencias.Hecho;
import lombok.Getter;

public class SolicitudEliminacion extends Solicitud{
  @Getter
  public String motivo;

  public SolicitudEliminacion(String hechoAfectado, String motivo) {
    super(hechoAfectado, EstadoSolicitud.PENDIENTE); //por defecto se inicializan pendientes

    if (motivo == null || motivo.length() < 10) { //TODO: Cambio de 500 a 10
      throw new IllegalArgumentException("El motivo debe tener al menos 10 caracteres.");
    }

    if (DetectorDeSpam.esSpam(motivo)) {
      this.estado = EstadoSolicitud.RECHAZADA;
      return;
    }

    this.motivo = motivo;
  }

  @Override
  public void aceptarSolicitud(){
    super.aceptarSolicitud();
   // hechoAfectado.setEliminado(true);
  }
  public void rechazarSolicitud(){
    super.rechazarSolicitud();
  }
}