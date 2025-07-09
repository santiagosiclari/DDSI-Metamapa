package metemapaColecciones.persistencia;
import domain.business.criterio.Coleccion;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioColecciones {
  private final Map<UUID, Coleccion> colecciones = new ConcurrentHashMap<>();

  public void save(Coleccion coleccion) {
    colecciones.put(coleccion.getHandle(), coleccion);
  }

  public Optional<Coleccion> findById(UUID id) {
    return Optional.ofNullable(colecciones.get(id));
  }

  public List<Coleccion> obtenerTodas() {
    return new ArrayList<>(colecciones.values());
  }

  public boolean update(Coleccion coleccion) {
    return colecciones.replace(coleccion.getHandle(), coleccion) != null;
  }

  public boolean eliminar(UUID id) {
    return colecciones.remove(id) != null;
  }

  public boolean contiene(UUID id) {
    return colecciones.containsKey(id);
  }
}