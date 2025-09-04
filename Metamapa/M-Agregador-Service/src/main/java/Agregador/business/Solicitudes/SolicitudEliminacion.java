package Agregador.business.Solicitudes;
import lombok.Getter;

import java.util.UUID;

public class SolicitudEliminacion extends Solicitud {
  @Getter
  public String motivo;
  static private Integer contadorID = 1;
  public SolicitudEliminacion(Integer hechoAfectado, String motivo){
    super(hechoAfectado, EstadoSolicitud.PENDIENTE); //por defecto se inicializan pendientes

    Boolean esSpam;
    try {
     esSpam = DetectorDeSpam.esSpam(motivo);
    }
   catch (Exception e) {
      //a revisar que hacer en caso de que la API falle para detectar el spam
      esSpam = true;
   }
    if(esSpam)this.estado = EstadoSolicitud.RECHAZADA;
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