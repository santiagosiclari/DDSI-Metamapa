package Agregador.persistencia;
import Agregador.business.Solicitudes.*;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RepositorioSolicitudesEdicion {
  // Lista en memoria para almacenar las solicitudes
  private List<SolicitudEdicion> solicitudesEdicion = new ArrayList<>();

  // Metodo para obtener todas las solicitudes de edicion
  public List<SolicitudEdicion> findAll() {
    return solicitudesEdicion;  // Devuelve la lista completa de solicitudes
  }

  // Metodo para guardar una solicitud
  public void save(SolicitudEdicion solicitud) {
    Optional<SolicitudEdicion> existingSolicitud = findById(solicitud.getId());
    existingSolicitud.ifPresent(SolicitudEdicion -> solicitudesEdicion.remove(SolicitudEdicion));
    solicitudesEdicion.add(solicitud);  // Luego, agregamos la nueva versión
  }

  // Metodo para encontrar una solicitud por ID
  public Optional<SolicitudEdicion> findById(Integer id) {
    return solicitudesEdicion.stream()
            .filter(solicitud -> solicitud.getId().equals(id))  // Compara el ID de la solicitud
            .findFirst();  // Devuelve el primer resultado, si lo encuentra
  }

  public ArrayList<SolicitudEdicion> getSolicitudesEdicionPendientes() {
    return solicitudesEdicion.stream().filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.PENDIENTE).collect(Collectors.toCollection(ArrayList::new));
  }

  public ArrayList<SolicitudEdicion> getSolicitudesEdicionAprobadas(){
    return solicitudesEdicion.stream().filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.APROBADA).collect(Collectors.toCollection(ArrayList::new));
  }
}