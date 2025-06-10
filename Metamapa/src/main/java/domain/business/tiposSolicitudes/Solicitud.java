package domain.business.tiposSolicitudes;
import domain.business.incidencias.Hecho;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public abstract class Solicitud {
  // Atributos
  Hecho hechoAfectado;  // Hecho relacionado con la solicitud
  EstadoSolicitud estado; // Estado de la solicitud (puede ser un enum o clase)

  // Constructor
  public Solicitud(Hecho hechoAfectado, EstadoSolicitud estado) {
    this.hechoAfectado = hechoAfectado;
    this.estado = estado;
  }

  public void aceptarSolicitud(){
    this.estado = EstadoSolicitud.APROBADA;
  }
  public  void rechazarSolicitud(){
    this.estado = EstadoSolicitud.RECHAZADA;
  }
}