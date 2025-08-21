package Agregador.business.Colecciones;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;
import Agregador.business.Hechos.TipoMultimedia;

public class CriterioMultimedia implements Criterio {
  @Getter
  private TipoMultimedia tipoMultimedia;

  public CriterioMultimedia(TipoMultimedia tipoMultimedia) {
    this.tipoMultimedia = tipoMultimedia;
  }

  @Override
  public boolean cumple(Hecho hecho) {
    return hecho.getMultimedia().stream().anyMatch(m-> getTipoMultimedia() == this.getTipoMultimedia());
  }
}