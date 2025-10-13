package Agregador.business.Colecciones;

import lombok.Getter;
import Agregador.business.Hechos.Hecho;
import java.util.Objects;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


public class CriterioFuenteDeDatos extends Criterio {
  @Getter
  private Integer idFuenteDeDatos;

  public CriterioFuenteDeDatos(Integer idFuenteDeDatos, boolean inclusion) {
    this.idFuenteDeDatos = idFuenteDeDatos;
    this.inclusion = inclusion;
  }

  @Override
  public boolean cumple(Hecho hechoAValidar){
    //BigInteger idFuenteDeDatosAValidar = hechoAValidar.getId();
    //return this.getIdFuenteDeDatos().equals(idFuenteDeDatosAValidar);
    return inclusion == Objects.equals(hechoAValidar.getIdFuente(), this.idFuenteDeDatos);
  }

  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    //todo arreglar porque no existe el campo fuente de datos en la tabla
    Predicate igual = cb.equal(root.get("idFuente"), idFuenteDeDatos);
    return inclusion ? igual : cb.not(igual);
  }
}