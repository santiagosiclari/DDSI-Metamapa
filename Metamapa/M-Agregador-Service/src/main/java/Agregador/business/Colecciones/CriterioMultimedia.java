package Agregador.business.Colecciones;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;
import Agregador.business.Hechos.TipoMultimedia;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


public class CriterioMultimedia extends Criterio {
  @Getter
  private TipoMultimedia tipoMultimedia;

  public CriterioMultimedia(TipoMultimedia tipoMultimedia, boolean inclusion) {
    this.tipoMultimedia = tipoMultimedia;
    this.inclusion = inclusion;
  }

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