package Metamapa.business.Solicitudes;
import lombok.Getter;

public class SolicitudEliminacion extends Solicitud {
  @Getter
  public String motivo;
  static private Integer contadorID = 1;
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
    this.id = contadorID++;
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