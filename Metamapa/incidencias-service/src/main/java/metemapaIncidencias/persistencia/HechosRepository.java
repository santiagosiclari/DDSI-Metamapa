package metemapaIncidencias.persistencia;
import domain.business.incidencias.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HechosRepository {
  private List<Hecho> hechos = new ArrayList<>();

  public List<Hecho> findAll() {
    return hechos;  // Devuelve la lista completa de hechos
  }


  public void save(Hecho hecho) {
    hechos.add(hecho);  // Agrega un hecho a la lista
  }


  public Optional<Hecho> findById(int id) {
    return Optional.ofNullable((Hecho) hechos.get(id));
  }

}