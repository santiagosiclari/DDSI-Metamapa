package Agregador.business.Colecciones;
import jakarta.persistence.Entity;
import lombok.Getter;
import Agregador.business.Hechos.*;
import jakarta.persistence.criteria.*;

@Entity
public class CriterioMultimedia extends Criterio {
  @Getter
  private TipoMultimedia tipoMultimedia;

  public CriterioMultimedia(TipoMultimedia tipoMultimedia, boolean inclusion) {
    this.tipoMultimedia = tipoMultimedia;
    this.inclusion = inclusion;
  }

  public CriterioMultimedia() {}

  @Override
  public boolean cumple(Hecho hecho) {
    return inclusion == hecho.getMultimedia().stream().anyMatch(m-> getTipoMultimedia() == this.getTipoMultimedia());
  }

  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    // Join con la colección de multimedia
    Join<Object, Object> multimediaJoin = root.join("multimedia", JoinType.LEFT);

    Predicate tipoIgual = cb.equal(multimediaJoin.get("tipoMultimedia"), tipoMultimedia);

    return inclusion ? tipoIgual : cb.not(tipoIgual);
  }
}