package metemapaSolicitudes.Service;
import DTO.SolicitudEdicionDTO;
import domain.business.tiposSolicitudes.EstadoSolicitud;
import domain.business.tiposSolicitudes.SolicitudEdicion;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import metemapaSolicitudes.persistencia.RepositorioSolicitudEdicion;
import org.springframework.stereotype.Service;

@Service
public class ServiceSolicitudEdicion {
  private final RepositorioSolicitudEdicion solicitudEdicionRepository;

  public ServiceSolicitudEdicion(RepositorioSolicitudEdicion solicitudEdicionRepository) {
    this.solicitudEdicionRepository = solicitudEdicionRepository;
  }

  public List<SolicitudEdicion> obtenerTodas() {
    return solicitudEdicionRepository.findAll();
  }

  public SolicitudEdicionDTO crearSolicitudEdicion(SolicitudEdicionDTO dto) {
    // Podés agregar lógica de validación acá si querés (por ejemplo, usar el serviceIncidencia)
    SolicitudEdicion solicitud = new SolicitudEdicion(
        dto.getTituloMod(),
        dto.getDescMod(),
        dto.getCategoriaMod(),
        dto.getUbicacionMod(),
        dto.getFechaHechoMod(),
        dto.getMultimediaMod(),
        dto.getAnonimidadMod(),
        dto.getSugerencia(),
        dto.getHechoAfectado()
    );
    solicitudEdicionRepository.save(solicitud);
    return new SolicitudEdicionDTO(solicitud);
  }

  public SolicitudEdicionDTO actualizarEstadoSolicitudEdicion(Integer id, Map<String, Object> requestBody) {
    Optional<SolicitudEdicion> solicitudOpt = solicitudEdicionRepository.findById(id);
    if (solicitudOpt.isEmpty()) {
      return null;
    }
    SolicitudEdicion solicitud = solicitudOpt.get();
    String nuevoEstadoStr = (String) requestBody.get("estado");
    if (nuevoEstadoStr == null) {
      throw new IllegalArgumentException("Estado no proporcionado");
    }
    EstadoSolicitud nuevoEstado;
    try {
      nuevoEstado = EstadoSolicitud.valueOf(nuevoEstadoStr);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Estado inválido: " + nuevoEstadoStr);
    }
    solicitud.setEstado(nuevoEstado);
    solicitudEdicionRepository.save(solicitud);
    if (nuevoEstado == EstadoSolicitud.APROBADA) {
      // Llamás al servicio de incidencias para aplicar edición
      // serviceIncidencia.aplicarEdicionIncidencia(solicitud);
    }
    return new SolicitudEdicionDTO(solicitud);
  }

  public SolicitudEdicionDTO obtenerSolicitudEdicionPorId(Integer id) {
    Optional<SolicitudEdicion> solicitudOpt = solicitudEdicionRepository.findById(id);
    return solicitudOpt.map(SolicitudEdicionDTO::new).orElse(null);
  }
}