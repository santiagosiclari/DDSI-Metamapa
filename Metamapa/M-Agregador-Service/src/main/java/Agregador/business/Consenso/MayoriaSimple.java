package Agregador.business.Consenso;

import Agregador.business.Hechos.Hecho;
import java.util.ArrayList;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
//TODO linkear las fuentes de domain en este modulo o crear una carpeta con la clase a usar
@Entity
@DiscriminatorValue("MAYORIA_SIMPLE")
public class MayoriaSimple extends Consenso {
  //si al menos la mitad de las fuentes contienen el mismo hecho, se lo considera
  //consensuado;
  @Override
  public boolean esConsensuado(Hecho hecho, ArrayList<Hecho> hechos) {
    int apariciones = 0;
    int cantFuentes = Consenso.contarFuentesDeDatos(hechos);
      for (Hecho h : hechos) {
        if (Consenso.sonIguales(hecho,h)) {
          apariciones++;
          break;
        }
    }
    return apariciones >= (cantFuentes / 2.0);
  }
}