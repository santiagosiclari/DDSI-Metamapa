package domain.business.incidencias;
import lombok.Getter;

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