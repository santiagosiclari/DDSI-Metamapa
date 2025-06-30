package domain.business.tiposSolicitudes;
import domain.business.incidencias.Hecho;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
public abstract class Solicitud {
  // Atributos
  @Setter @Getter
  @JsonIgnore
  Hecho hechoAfectado;  // Hecho relacionado con la solicitud
  @Setter @Getter
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