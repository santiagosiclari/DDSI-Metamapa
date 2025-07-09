package metemapaSolicitudes.persistencia;

import domain.business.tiposSolicitudes.EstadoSolicitud;
import domain.business.tiposSolicitudes.SolicitudEdicion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RepositorioSolicitudEdicion {
  // Lista en memoria para almacenar las solicitudes
  private List<SolicitudEdicion> solicitudes = new ArrayList<>();

  // Metodo para obtener todas las solicitudes
  public List<SolicitudEdicion> findAll() {
    return solicitudes;  // Devuelve la lista completa de solicitudes
  }

  // Metodo para guardar una solicitud
  public void save(SolicitudEdicion solicitud) {
    solicitudes.add(solicitud);  // Agrega la solicitud a la lista
  }

  // Metodo para encontrar una solicitud por ID
  public Optional<SolicitudEdicion> findById(Integer id) {
    // Buscar en la lista de solicitudes usando el UUID
    return solicitudes.stream()
        .filter(solicitud -> solicitud.getId().equals(id))  // Compara el ID de la solicitud
        .findFirst();  // Devuelve el primer resultado, si lo encuentra
  }
  public ArrayList<SolicitudEdicion> getSolicitudesPendientes() {
    return solicitudes.stream().filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.PENDIENTE).collect(Collectors.toCollection(ArrayList::new));
  }

  public ArrayList<SolicitudEdicion> getSolicitudesAprobadas(){
    return solicitudes.stream().filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.APROBADA).collect(Collectors.toCollection(ArrayList::new));
  }
}
