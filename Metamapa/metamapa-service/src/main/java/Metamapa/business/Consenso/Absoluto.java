package Metamapa.business.Consenso;

import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.Hechos.Hecho;
import java.util.List;

public class Absoluto implements Consenso {
// si todas las fuentes contienen el mismo, se lo considera consensuado.
  @Override
  public boolean esConsensuado(Hecho hecho, List<FuenteDeDatos> fuentes) {
    for (FuenteDeDatos fuente : fuentes) {
      boolean contiene = fuente.getHechos().stream()
          .anyMatch(h -> h.equals(hecho));
      if (!contiene) return false;
    }
    return true;
  }
}