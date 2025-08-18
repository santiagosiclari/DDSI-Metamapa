package Metamapa.business.Colecciones;

import Metamapa.business.Hechos.Hecho;
import lombok.Getter;

public class CriterioDescripcion implements Criterio {
  @Getter
  private String descripcion;

  public CriterioDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
  @Override
  public boolean cumple(Hecho hechoAValidar){
    String descripcionAValidar = hechoAValidar.getDescripcion();

    return this.descripcion.equals(descripcionAValidar);
  }
}
