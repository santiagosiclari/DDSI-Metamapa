package FuenteEstatica.persistencia;

import FuenteEstatica.business.Hechos.*;
import java.util.*;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioHechos {
  @Getter
  private ArrayList<Hecho> hechos = new ArrayList<>();

  public void agregarHecho(Hecho h) {
    hechos.add(h);
  }

  public Hecho findHecho(int id) {
    return (hechos.stream().filter(h -> h.getId() == id).findFirst()).get();
  }

  public void modificarHecho(Hecho hecho) {
    // implementar, no creo que haga falta
  }
}