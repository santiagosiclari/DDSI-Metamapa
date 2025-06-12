package domain.business.criterio;
import domain.business.incidencias.Hecho;
import lombok.Getter;

public class CriterioOrigen implements Criterio{
  @Getter
  private String origen;

  public CriterioOrigen(String origen) {

    this.origen = origen;
  }
  public boolean cumple(Hecho hechoAValidar){
    String origenAValidar = String.valueOf(hechoAValidar.getFuenteDeDatos());

    return this.getOrigen().equals(origenAValidar.getClass().getName());
  }
}
