package domain.business.criterio;
import domain.business.incidencias.Hecho;

import lombok.Getter;


public class CriterioTitulo implements Criterio {
  @Getter
  private String titulo;

  public CriterioTitulo(String titulo) {
    this.titulo = titulo;
  }

  @Override
  public boolean cumple(Hecho hechoAValidar){
    String tituloAValidar = hechoAValidar.getTitulo();

    return this.getTitulo().equals(tituloAValidar);
  }
}

