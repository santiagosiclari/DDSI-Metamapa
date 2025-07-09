package domain.business.tiposSolicitudes;
import domain.business.incidencias.Hecho;
import lombok.Getter;
import lombok.Setter;

public abstract class Solicitud {
  @Setter @Getter
  //Hecho hechoAfectado;  // Hecho relacionado con la solicitud
  String hechoAfectado;
  @Setter @Getter
  EstadoSolicitud estado; // Estado de la solicitud (puede ser un enum o clase)
  @Getter
  int id;
  static public int contadorID = 1;

  public Solicitud(String hechoAfectado, EstadoSolicitud estado) {
    this.hechoAfectado = hechoAfectado;
    this.estado = estado;
    this.id = contadorID++;
  }

  public void aceptarSolicitud(){
    this.estado = EstadoSolicitud.APROBADA;
  }
  public  void rechazarSolicitud(){this.estado = EstadoSolicitud.RECHAZADA;
  }
}