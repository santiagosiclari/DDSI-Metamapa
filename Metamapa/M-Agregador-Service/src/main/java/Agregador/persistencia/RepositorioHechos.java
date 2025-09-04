package Agregador.persistencia;

import Agregador.business.Colecciones.Criterio;
import Agregador.business.Hechos.Hecho;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
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

  public Optional<Hecho> findHecho(BigInteger id) {
    return hechos.stream().filter(h -> h.getId().equals(id)).findFirst();
  }

  public void saveAll(Collection<Hecho> nuevos) {
    hechos.addAll(nuevos);
  }


  public void modificarHecho(Hecho hecho) {
    //TODO implementar, no creo que haga falta

    return (hechos.stream().filter(h -> h.getId().equals(id)).findFirst());
  }

  public void updateHecho(Hecho h) {
    Optional<Hecho> existingHechoOpt = findHecho(h.getId());
    existingHechoOpt.ifPresent(s -> hechos.remove(s));
    hechos.add(h);

  }

  public List<Hecho> filtrarPorCriterios(List<Criterio> inclusion, List<Criterio> exclusion) {
    return hechos.stream()
            .filter(h -> inclusion.stream().allMatch(c -> c.cumple(h)))
            .filter(h -> exclusion.stream().noneMatch(c -> c.cumple(h)))
            .toList();
  }
}