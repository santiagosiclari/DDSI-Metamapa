package Agregador.business.Solicitudes;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter @Getter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Solicitud {
  @ManyToOne (cascade = CascadeType.ALL)
  private Hecho hechoAfectado;
  @Enumerated(EnumType.STRING) // ← Esto le dice a JPA que guarde el nombre del enum
  private EstadoSolicitud estado;
  //UUID id;
  @Id
  protected Integer id;
  static protected Integer contadorID = 1;

  public Solicitud(Hecho hechoAfectado, EstadoSolicitud estado) {
    this.hechoAfectado = hechoAfectado;
    this.estado = estado;
    this.id = contadorID++;
    //this.id = UUID.randomUUID();
  }

  public Solicitud() {}

  public void aceptarSolicitud(){
    this.estado = EstadoSolicitud.APROBADA;
  }
  public  void rechazarSolicitud(){this.estado = EstadoSolicitud.RECHAZADA;
  }
}