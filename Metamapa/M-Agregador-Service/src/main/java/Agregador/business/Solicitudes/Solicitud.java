package Agregador.business.Solicitudes;
import lombok.*;
import java.math.BigInteger;

@Setter @Getter
public abstract class Solicitud {
  //Hecho hechoAfectado;  // Hecho relacionado con la solicitud
  BigInteger hechoAfectado;
  EstadoSolicitud estado; // Estado de la solicitud (puede ser un enum o clase)
  //UUID id;
  protected Integer id;
  static protected Integer contadorID = 1;

  public Solicitud(BigInteger hechoAfectado, EstadoSolicitud estado) {

    this.hechoAfectado = hechoAfectado;
    this.estado = estado;
    this.id = contadorID++;
    //this.id = UUID.randomUUID();
  }

  public void aceptarSolicitud(){
    this.estado = EstadoSolicitud.APROBADA;
  }
  public  void rechazarSolicitud(){this.estado = EstadoSolicitud.RECHAZADA;
  }
}