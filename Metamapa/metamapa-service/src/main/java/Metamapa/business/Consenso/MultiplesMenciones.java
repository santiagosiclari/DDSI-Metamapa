package Metamapa.business.Consenso;

import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.Hechos.Hecho;
import java.util.List;

public class MultiplesMenciones implements Consenso {
  //si al menos dos fuentes contienen un mismo hecho y ninguna otra fuente
  //contiene otro de igual título pero diferentes atributos, se lo considera consensuado;
  @Override
  public boolean esConsensuado(Hecho hecho, List<FuenteDeDatos> fuentes) {
    int aparicionesExactas = 0;
    boolean hayConflictos = false;
    for (FuenteDeDatos fuente : fuentes) {
      for (Hecho h : fuente.getHechos()) {
        if (h.getTitulo().equalsIgnoreCase(hecho.getTitulo())) {
          if (h.equals(hecho)) {
            aparicionesExactas++;
          } else {
            hayConflictos = true;
          }
        }
      }
    }
    return aparicionesExactas >= 2 && !hayConflictos;
  }
}