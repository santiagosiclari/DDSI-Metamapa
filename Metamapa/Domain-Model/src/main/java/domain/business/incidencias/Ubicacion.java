package domain.business.incidencias;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
public class Ubicacion {
  @Getter
  private Float latitud;
  @Getter
  private Float longitud;
  public Ubicacion(){}

  public Ubicacion(Float latitud, Float longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }

  public boolean esIgual(Float latitudAValidar, Float longitudAValidar) {
    return latitudAValidar.equals(this.getLatitud()) && longitudAValidar.equals(this.getLongitud());
  }
}