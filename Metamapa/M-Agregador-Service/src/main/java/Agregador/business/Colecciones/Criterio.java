package Agregador.business.Colecciones;
import jakarta.persistence.criteria.*;
import lombok.*;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
public abstract class Criterio {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "criterio_seq")
  @SequenceGenerator(name = "criterio_seq", sequenceName = "criterio_seq", allocationSize = 1)
  private Integer id;
  @Column(nullable = false)
  Boolean inclusion;

  public Criterio() {}

  public Criterio(Boolean inclusion) {
    this.inclusion = inclusion;
  }

  public boolean cumple(Hecho hecho) {
    return inclusion;
  }

  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    return null;
  }
}