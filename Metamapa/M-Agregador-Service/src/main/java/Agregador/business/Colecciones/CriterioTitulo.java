package Agregador.business.Colecciones;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


public class CriterioTitulo extends Criterio {
  @Getter
  private final String titulo;

  public CriterioTitulo(String titulo, boolean inclusion) {
    this.titulo = titulo;
    this.inclusion = inclusion;
  }

  @Override
  public boolean cumple(Hecho hechoAValidar){
    String tituloAValidar = hechoAValidar.getTitulo();
    return inclusion == this.getTitulo().equals(tituloAValidar);
  }

  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    Predicate igual = cb.equal(root.get("titulo"), titulo);
    return inclusion ? igual : cb.not(igual);
  }

}