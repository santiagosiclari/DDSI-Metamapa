package Metamapa.business.Colecciones;

import Metamapa.business.Hechos.Hecho;
import lombok.Getter;

import java.math.BigInteger;


public class CriterioFuenteDeDatos implements Criterio {
  @Getter
  private Integer idFuenteDeDatos;

  public CriterioFuenteDeDatos(Integer idFuenteDeDatos)
  {
    this.idFuenteDeDatos = idFuenteDeDatos;
  }
  @Override
  public boolean cumple(Hecho hechoAValidar){
    BigInteger idFuenteDeDatosAValidar = hechoAValidar.getId();

    return this.getIdFuenteDeDatos().equals(idFuenteDeDatosAValidar);
  }
}

