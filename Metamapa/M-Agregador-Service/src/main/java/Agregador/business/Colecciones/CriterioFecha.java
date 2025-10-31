package Agregador.business.Colecciones;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.criteria.*;

@Entity
@Getter
public class CriterioFecha extends Criterio {
  public LocalDateTime fechaDesde;
  public LocalDateTime fechaHasta;

  public CriterioFecha(LocalDateTime fechaDesde, LocalDateTime fechaHasta,boolean inclusion) {
    this.fechaDesde = fechaDesde;
    this.fechaHasta = fechaHasta;
    this.inclusion = inclusion;
  }

  public CriterioFecha() {}

  public boolean cumple(Hecho hechoAValidar) {
    LocalDateTime fechaAValidar = hechoAValidar.getFechaHecho();
    if (this.getFechaHasta() == null) return !fechaAValidar.isBefore(this.getFechaDesde());
    if (this.getFechaDesde() == null) return !fechaAValidar.isAfter(this.getFechaHasta());
    return inclusion == (!fechaAValidar.isBefore(this.getFechaDesde()) && !fechaAValidar.isAfter(this.getFechaHasta()));
  }
  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    Predicate predicate = null;
    if (fechaDesde != null && fechaHasta != null) {
      predicate = cb.between(root.get("fechaHecho"), fechaDesde, fechaHasta);
    } else if (fechaDesde != null) {
      predicate = cb.greaterThanOrEqualTo(root.get("fechaHecho"), fechaDesde);
    } else if (fechaHasta != null) {
      predicate = cb.lessThanOrEqualTo(root.get("fechaHecho"), fechaHasta);
    }

    if (predicate == null) {
      return cb.conjunction();
    }

    return inclusion ? predicate : cb.not(predicate);
  }
}