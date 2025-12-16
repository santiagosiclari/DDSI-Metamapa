package FuenteEstatica.persistencia;
import FuenteEstatica.business.Hechos.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioHechos {
  private final ArrayList<Hecho> hechos = new ArrayList<>();

  public void save(Hecho h) {
    hechos.add(h);
  }

  public Hecho findById(int id) {
    return (hechos.stream().filter(h -> h.getId() == id).findFirst()).get();
  }

  public void modificarHecho(Hecho hecho) {
  }
}