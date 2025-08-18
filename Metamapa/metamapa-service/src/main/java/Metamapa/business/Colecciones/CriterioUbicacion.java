package Metamapa.business.Colecciones;
import Metamapa.business.Hechos.Hecho;
import java.util.Objects;
import lombok.Getter;

public class CriterioUbicacion implements Criterio {
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
    /*Ubicacion ubicacionAValidar = hechoAValidar.getUbicacion();
    return ubicacionAValidar.esIgual(this.getLatitud(), this.getLongitud());*/
    //TODO: APROXIMAR UN RANGO
    return Objects.equals(hechoAValidar.getLatitud(), latitud) && Objects.equals(hechoAValidar.getLongitud(), longitud);
  }
}