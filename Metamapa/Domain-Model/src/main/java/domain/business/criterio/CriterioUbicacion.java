package domain.business.criterio;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Ubicacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Criterio_Ubicacion")
public class CriterioUbicacion implements Criterio{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "critUbicacion_coleccion")
  private Long critUbicacion_coleccion;

  @Column(name = "critUbicacion_fuente")
  private Long critUbicacion_fuente;
  @Column(name = "critUbicacion_inclusion")
  private Boolean critUbicacion_inclusion;

  private Float latitud;
  private Float longitud;

  public CriterioUbicacion(Float latitud, Float longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }

  @Override
  public boolean cumple(Hecho hechoAValidar) {
    Ubicacion ubicacionAValidar = hechoAValidar.getUbicacion();
    return ubicacionAValidar.esIgual(this.getLatitud(), this.getLongitud());
  }
}
