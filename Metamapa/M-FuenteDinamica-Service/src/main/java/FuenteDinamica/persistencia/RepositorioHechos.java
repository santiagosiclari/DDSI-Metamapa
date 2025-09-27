package FuenteDinamica.persistencia;
import FuenteDinamica.business.Hechos.Hecho;
import org.springframework.stereotype.Repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioHechos extends JpaRepository<Hecho, Integer> {
  /*private final ArrayList<Hecho> hechos = new ArrayList<>();

  public List<Hecho> findAll() {
    return hechos;
  }

  public Optional<Hecho> findById(Integer id) {
    return hechos.stream().filter(h -> h.getId().equals(id)).findFirst();
  }

  public void save(Hecho h) {
    hechos.add(h);
  }

  public void updateHecho(Hecho h) {
    Optional<Hecho> existingHechoOpt = findById(h.getId());
    existingHechoOpt.ifPresent(hechos::remove);
    hechos.add(h);
  }*/
}