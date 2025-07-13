package domain.business.criterio;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.TipoMultimedia;
import lombok.Getter;

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