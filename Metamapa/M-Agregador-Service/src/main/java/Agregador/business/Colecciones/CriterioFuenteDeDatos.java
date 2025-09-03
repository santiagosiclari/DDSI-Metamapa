package Agregador.business.Colecciones;

import lombok.Getter;
import Agregador.business.Hechos.Hecho;

import java.math.BigInteger;
import java.util.Objects;

public class CriterioFuenteDeDatos implements Criterio {
  @Getter
  private Integer idFuenteDeDatos;

  public CriterioFuenteDeDatos(Integer idFuenteDeDatos) {
    this.idFuenteDeDatos = idFuenteDeDatos;
  }

  @Override
  public boolean cumple(Hecho hechoAValidar){
    //BigInteger idFuenteDeDatosAValidar = hechoAValidar.getId();
    //return this.getIdFuenteDeDatos().equals(idFuenteDeDatosAValidar);
    return Objects.equals(hechoAValidar.getIdFuente(), this.idFuenteDeDatos);
  }
}

