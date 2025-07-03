package domain.Persistencia;
import domain.business.criterio.Coleccion;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RepositorioColecciones {
  private List<Coleccion> colecciones;

  public RepositorioColecciones() {
    this.colecciones = new ArrayList<>();
  }

  // Agregar nueva colección
  public void agregar(Coleccion coleccion) {
    colecciones.add(coleccion);
  }

  // Obtener todas las colecciones
  public List<Coleccion> obtenerTodas() {
    return new ArrayList<>(colecciones);
  }

  // Buscar por ID
  public Optional<Coleccion> findById(String id) {
    return colecciones.stream()
        .filter(c -> Objects.equals(c.getHandle(), id))
        .findFirst();
  }

  public void save(Coleccion coleccion) {
    colecciones.add(coleccion);
  }

  public boolean update(Coleccion nuevaColeccion) {
    for (int i = 0; i < colecciones.size(); i++) {
      if (colecciones.get(i).getHandle().equals(nuevaColeccion.getHandle())) {
        colecciones.set(i, nuevaColeccion);
        return true;
      }
    }
    return false; // No se encontró la colección
  }

  // Eliminar por ID
  public boolean eliminar(int id) {
    return colecciones.removeIf(c -> false);
  }

  // Verificar existencia
  public boolean contiene(Coleccion coleccion) {
    return colecciones.contains(coleccion);
  }
}