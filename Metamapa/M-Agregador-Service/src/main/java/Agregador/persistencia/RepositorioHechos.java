package Agregador.persistencia;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Hechos.Hecho;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
import java.util.*;

@Repository
public class RepositorioHechos {
  private ArrayList<Hecho> hechos = new ArrayList<>();

  public List<Hecho> findAll() {
    return hechos;
  }

  public void save(Hecho h) {
    hechos.add(h);
  }

  public Optional<Hecho> findById(BigInteger id) {
    return hechos.stream().filter(h -> h.getId().equals(id)).findFirst();
  }

  public List<Hecho> findByCategoriaAndEliminadoFalse(String categoria) {
    return hechos.stream()
            .filter(h -> h.getCategoria() != null
                    && h.getCategoria().equalsIgnoreCase(categoria))
            .filter(h -> h.getEliminado() == null || !h.getEliminado())
            .toList();
  }

  public void saveAll(Collection<Hecho> nuevos) {
    hechos.addAll(nuevos);
  }

  public void modificarHecho(Hecho hecho) {
    // implementar, no creo que haga falta

    //return (hechos.stream().filter(h -> h.getId().equals(id)).findFirst());
  }

  public void updateHecho(Hecho h) {
    Optional<Hecho> existingHechoOpt = findById(h.getId());
    existingHechoOpt.ifPresent(s -> hechos.remove(s));
    hechos.add(h);

  }

  public List<Hecho> filtrarPorCriterios(List<Criterio> criterios) {
    return hechos.stream()
        .filter(h -> criterios.stream().allMatch(c -> c.cumple(h)))
        .toList();
  }
}