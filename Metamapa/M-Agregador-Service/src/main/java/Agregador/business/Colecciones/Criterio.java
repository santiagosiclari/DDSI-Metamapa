package Agregador.business.Colecciones;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Criterio {
  @Id
  Integer id;
  @Getter
  boolean inclusion;

  public boolean cumple(Hecho hecho) {
    return inclusion;
  }

  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb)
  {
    return null;
  }

}