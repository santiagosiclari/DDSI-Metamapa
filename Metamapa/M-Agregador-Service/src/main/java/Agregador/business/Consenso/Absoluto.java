package Agregador.business.Consenso;

import Agregador.business.Hechos.Hecho;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.ArrayList;

@Entity
@DiscriminatorValue("ABSOLUTO")
public class Absoluto extends Consenso {
// si todas las fuentes contienen el mismo, se lo considera consensuado.
  @Override
  public boolean esConsensuado(Hecho hecho, ArrayList<Hecho> hechos) {
    int apariciones = 0;
    int cantFuentes = Consenso.contarFuentesDeDatos(hechos);
    for (Hecho h : hechos) {
      if (Consenso.sonIguales(hecho, h)) {
        apariciones++;
        break;
      }
    }
    return apariciones >= cantFuentes;
  }
}