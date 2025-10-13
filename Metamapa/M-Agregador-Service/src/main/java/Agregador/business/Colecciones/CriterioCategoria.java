package Agregador.business.Colecciones;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;


public class CriterioCategoria extends Criterio {
  @Getter
  private String categoria;

  public CriterioCategoria(String categoria, boolean inclusion) {
    this.categoria = categoria;
    this.inclusion = inclusion;
  }

  @Override
  public boolean cumple(Hecho hechoAValidar){
    String categoriaAValidar = hechoAValidar.getCategoria();
    return inclusion == this.getCategoria().equalsIgnoreCase(categoriaAValidar);
  }
  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    Predicate igual = cb.equal(cb.lower(root.get("categoria")), categoria.toLowerCase());
    return inclusion ? igual : cb.not(igual);
  }
}
