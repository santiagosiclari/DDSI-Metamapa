package metemapaSolicitudes.Service;
import DTO.SolicitudEliminacionDTO;
import domain.business.tiposSolicitudes.EstadoSolicitud;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import metemapaSolicitudes.persistencia.RepositorioSolicitudEliminacion;
import org.springframework.stereotype.Service;

@Service
public class ServiceSolicitudEliminacion {
  private final RepositorioSolicitudEliminacion solicitudEliminacionRepository;

  public ServiceSolicitudEliminacion(RepositorioSolicitudEliminacion solicitudEliminacionRepository) {
    this.solicitudEliminacionRepository = solicitudEliminacionRepository;
  }

  public List<SolicitudEliminacionDTO> obtenerTodasSolicitudesEliminacion() {
    return solicitudEliminacionRepository.findAll()
        .stream()
        .map(SolicitudEliminacionDTO::new)
        .collect(Collectors.toList());
  }

  public Optional<SolicitudEliminacionDTO> obtenerPorId(Integer id) {
    return solicitudEliminacionRepository.findById(id)
        .map(SolicitudEliminacionDTO::new);
  }

  public SolicitudEliminacionDTO crearSolicitud(SolicitudEliminacionDTO dto) {
    SolicitudEliminacion solicitud = new SolicitudEliminacion(dto.getHechoAfectado(), dto.getMotivo());
    solicitudEliminacionRepository.save(solicitud);
    return new SolicitudEliminacionDTO(solicitud);
  }

  public SolicitudEliminacionDTO actualizarEstado(Integer id, Map<String, Object> requestBody) {
    SolicitudEliminacion solicitud = solicitudEliminacionRepository.findById(id).orElseThrow(NoSuchElementException::new);
    String nuevoEstado = (String) requestBody.get("estado");
    if (nuevoEstado == null || nuevoEstado.isBlank()) {
      throw new IllegalArgumentException("Estado inválido");
    }
    solicitud.setEstado(EstadoSolicitud.valueOf(nuevoEstado.toUpperCase()));
    solicitudEliminacionRepository.save(solicitud);
    return new SolicitudEliminacionDTO(solicitud);
  }
}