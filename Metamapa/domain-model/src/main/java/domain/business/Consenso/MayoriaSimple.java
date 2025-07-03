package domain.business.Consenso;

import domain.business.FuentesDeDatos.FuenteDeDatos;
import domain.business.incidencias.Hecho;
import java.util.List;

public class MayoriaSimple implements Consenso{
  //si al menos la mitad de las fuentes contienen el mismo hecho, se lo considera
  //consensuado;
  @Override
  public boolean esConsensuado(Hecho hecho, List<FuenteDeDatos> fuentes) {
    int apariciones = 0;
    for (FuenteDeDatos fuente : fuentes) {
      for (Hecho h : fuente.getHechos()) {
        if (h.equals(hecho)) {
          apariciones++;
          break;
        }
      }
    }
    return apariciones >= (fuentes.size() / 2.0);
  }
}