package Agregador.persistencia;
import Agregador.business.Solicitudes.*;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RepositorioSolicitudesEliminacion {
  private ArrayList<SolicitudEliminacion> solicitudesEliminacion = new ArrayList<>();

  // Metodo para obtener todas las solicitudes
  public List<SolicitudEliminacion> findAll() {
    return solicitudesEliminacion;  // Devuelve la lista completa de solicitudes
  }

  // Metodo para obtener todas las solicitudes de eliminacion que sean spam
  public List<SolicitudEliminacion> findAllSolicitudesEliminacionSpam() {
    return solicitudesEliminacion.stream()
            .filter(solicitudEliminacion -> solicitudEliminacion.getEstado() == EstadoSolicitud.SPAM)
            .toList();
  }

  // Metodo para guardar una solicitud
  public void save(SolicitudEliminacion solicitud) {
    Optional<SolicitudEliminacion> existingSolicitud = findById(solicitud.getId());
    existingSolicitud.ifPresent(solicitudEliminacion -> solicitudesEliminacion.remove(solicitudEliminacion));
    solicitudesEliminacion.add(solicitud);  // Luego, agregamos la nueva versión
  }

  // Metodo para encontrar una solicitud por ID
  public Optional<SolicitudEliminacion> findById(Integer id) {
    // Buscar en la lista de solicitudes usando el UUID
    return solicitudesEliminacion.stream()
            .filter(solicitud -> solicitud.getId().equals(id))  // Compara el ID de la solicitud
            .findFirst();  // Devuelve el primer resultado, si lo encuentra
  }
  public ArrayList<SolicitudEliminacion> getSolicitudesPendientes() {
    return solicitudesEliminacion.stream().filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.PENDIENTE).collect(Collectors.toCollection(ArrayList::new));
  }

  public ArrayList<SolicitudEliminacion> getSolicitudesAprobadas(){
    return solicitudesEliminacion.stream().filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.APROBADA).collect(Collectors.toCollection(ArrayList::new));
  }
  // Metodo para eliminar una solicitud
    /*public boolean deleteById(String id) {
        return solicitudes.removeIf(s -> s.getId().equals(id));  // Elimina la solicitud por ID
    }*/
}