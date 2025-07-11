package domain.business.criterio;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Ubicacion;
import lombok.Getter;

public class CriterioUbicacion implements Criterio{
  @Getter
  private Float latitud;
  @Getter
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
