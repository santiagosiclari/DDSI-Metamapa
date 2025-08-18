package Metamapa.business.Colecciones;
import Metamapa.business.Hechos.Hecho;
import lombok.Getter;

public class CriterioCategoria implements Criterio {
  @Getter
  private String categoria;

  public CriterioCategoria(String categoria) {
    this.categoria = categoria;
  }
  @Override
  public boolean cumple(Hecho hechoAValidar){
    String categoriaAValidar = hechoAValidar.getCategoria();

    return this.getCategoria().equalsIgnoreCase(categoriaAValidar);
  }
}
