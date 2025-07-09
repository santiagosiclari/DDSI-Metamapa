package metemapaSolicitudes.persistencia;
import domain.business.tiposSolicitudes.EstadoSolicitud;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
  public Optional<SolicitudEliminacion> findById(UUID id) {
    // Buscar en la lista de solicitudes usando el UUID
    return solicitudes.stream()
        .filter(solicitud -> solicitud.getId().equals(id))  // Compara el ID de la solicitud
        .findFirst();  // Devuelve el primer resultado, si lo encuentra
  }
  public ArrayList<SolicitudEliminacion> getSolicitudesPendientes() {
    return solicitudes.stream().filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.PENDIENTE).collect(Collectors.toCollection(ArrayList::new));
  }

  public ArrayList<SolicitudEliminacion> getSolicitudesAprobadas(){
    return solicitudes.stream().filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.APROBADA).collect(Collectors.toCollection(ArrayList::new));
  }
  // Metodo para eliminar una solicitud
    /*public boolean deleteById(String id) {
        return solicitudes.removeIf(s -> s.getId().equals(id));  // Elimina la solicitud por ID
    }*/
}