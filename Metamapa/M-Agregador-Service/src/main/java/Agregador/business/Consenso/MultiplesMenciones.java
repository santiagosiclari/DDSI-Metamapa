package Agregador.business.Consenso;
import Agregador.business.Hechos.Hecho;
import java.util.ArrayList;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MULTIPLES_MENCIONES")
public class MultiplesMenciones extends Consenso {
  public MultiplesMenciones() {
    super("MultiplesMenciones");
  }

  //si al menos dos fuentes contienen un mismo hecho y ninguna otra fuente
  //contiene otro de igual título pero diferentes atributos, se lo considera consensuado;
  @Override
  public boolean esConsensuado(Hecho hecho, ArrayList<Hecho> hechos) {
    int aparicionesExactas = 0;
    boolean hayConflictos = false;
    for (Hecho h : hechos) {
      if (Consenso.sonIguales(h, hecho)) {
        //TODO hacer algo que compare descripciones bien
        if (h.getDescripcion().equals(hecho.getDescripcion())) aparicionesExactas++;
        else hayConflictos = true;
      }
    }
    return aparicionesExactas >= 2 && !hayConflictos;
  }
}