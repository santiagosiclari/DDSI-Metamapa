package FuenteDinamica.persistencia;
import FuenteDinamica.business.Hechos.Hecho;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class RepositorioHechos {
  private final ArrayList<Hecho> hechos = new ArrayList<>();

  public List<Hecho> getHechos() {
    return hechos;
  }

  public Optional<Hecho> findHechoById(Integer id) {
    return hechos.stream().filter(h -> h.getId().equals(id)).findFirst();
  }

  public void save(Hecho h) {
    hechos.add(h);
  }

  public void updateHecho(Hecho h) {
    Optional<Hecho> existingHechoOpt = findHechoById(h.getId());
    existingHechoOpt.ifPresent(hechos::remove);
    hechos.add(h);
  }
}
