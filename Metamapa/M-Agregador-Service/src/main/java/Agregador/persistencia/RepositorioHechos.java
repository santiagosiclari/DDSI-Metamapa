package Agregador.persistencia;

import Agregador.business.Colecciones.Criterio;
import Agregador.business.Hechos.Hecho;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class RepositorioHechos {
  private ArrayList<Hecho> hechos = new ArrayList<>();

  public List<Hecho> getHechos() {
    return hechos;
  }
  public void save(Hecho h) {
    hechos.add(h);
  }

  public Hecho findHecho(int id) {
    return (hechos.stream().filter(h -> h.getId().equals(id)).findFirst()).get();
  }

  public void modificarHecho(Hecho hecho) {
    //TODO implementar, no creo que haga falta
  }

  public List<Hecho> filtrarPorCriterios(List<Criterio> inclusion, List<Criterio> exclusion) {
    return hechos.stream()
            .filter(h -> inclusion.stream().allMatch(c -> c.cumple(h)))
            .filter(h -> exclusion.stream().noneMatch(c -> c.cumple(h)))
            .toList();
  }
}