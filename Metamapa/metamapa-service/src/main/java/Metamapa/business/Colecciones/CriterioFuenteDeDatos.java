package Metamapa.business.Colecciones;

import Metamapa.business.Hechos.Hecho;
import lombok.Getter;



public class CriterioFuenteDeDatos implements Criterio {
  @Getter
  private Integer idFuenteDeDatos;

  public CriterioFuenteDeDatos(Integer idFuenteDeDatos)
  {
    this.idFuenteDeDatos = idFuenteDeDatos;
  }
  @Override
  public boolean cumple(Hecho hechoAValidar){
    Integer idFuenteDeDatosAValidar = hechoAValidar.getId();

    return this.getIdFuenteDeDatos().equals(idFuenteDeDatosAValidar);
  }
}

