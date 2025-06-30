package domain.Persistencia;

import domain.business.tiposSolicitudes.SolicitudEliminacion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioSolicitudEliminacion {
  // Lista en memoria para almacenar las solicitudes
  private List<SolicitudEliminacion> solicitudes = new ArrayList<>();

  // Metodo para obtener todas las solicitudes
  public List<SolicitudEliminacion> findAll() {
    return solicitudes;  // Devuelve la lista completa de solicitudes
  }

  // Metodo para guardar una solicitud
  public void save(SolicitudEliminacion solicitud) {
    solicitudes.add(solicitud);  // Agrega la solicitud a la lista
  }

  // Metodo para encontrar una solicitud por ID
  public Optional<SolicitudEliminacion> findById(String id) {
    return Optional.ofNullable(solicitudes.getFirst()); // corregir
    //.stream()
    //.filter(s -> s.getId().equals(id))
    //.findFirst();
  }

  // Metodo para eliminar una solicitud
    /*public boolean deleteById(String id) {
        return solicitudes.removeIf(s -> s.getId().equals(id));  // Elimina la solicitud por ID
    }*/
}