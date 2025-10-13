package Agregador.business.Colecciones;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;
public class CriterioDescripcion extends Criterio {
  @Getter
  private String descripcion;

  public CriterioDescripcion(String descripcion, boolean inclusion) {
    this.descripcion = descripcion;
    this.inclusion = inclusion;
  }
  @Override
  public boolean cumple(Hecho hechoAValidar){
    String descripcionAValidar = hechoAValidar.getDescripcion();
    return inclusion == this.descripcion.equals(descripcionAValidar);
  }
  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    Predicate igual = cb.equal(root.get("descripcion"), descripcion);
    return inclusion ? igual : cb.not(igual);
  }
}
