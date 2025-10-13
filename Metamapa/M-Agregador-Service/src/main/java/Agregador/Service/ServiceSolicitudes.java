package Agregador.Service;
import Agregador.DTO.SolicitudEdicionDTO;
import Agregador.DTO.SolicitudEliminacionDTO;
import Agregador.business.Hechos.Hecho;
import Agregador.business.Solicitudes.*;
import Agregador.persistencia.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class ServiceSolicitudes {
    public enum Result { OK, NOT_FOUND, CONFLICT, INVALID }
    private final RepositorioSolicitudesEliminacion repoSolicitudesEliminacion;
    private final RepositorioSolicitudesEdicion repoSolicitudesEdicion;
    private final RepositorioHechos repoHechos;

    public ServiceSolicitudes(RepositorioSolicitudesEliminacion repoEliminacion, RepositorioSolicitudesEdicion repoEdicion, RepositorioHechos rh) {
        this.repoSolicitudesEliminacion = repoEliminacion;
        this.repoSolicitudesEdicion = repoEdicion;
        this.repoHechos = rh;
    }

    // @Transactional
    public Result aprobar(Integer id) {
        SolicitudEliminacion s = repoSolicitudesEliminacion.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return Result.CONFLICT;
        s.aceptarSolicitud(); // cambia a APROBADA, timestamps, etc.
        //repoAgregador.bloquearHecho(s.getHechoAfectado()); // no mostrar / no re-ingestar
        repoSolicitudesEliminacion.save(s);
        return Result.OK;
    }

    // @Transactional
    public Result rechazar(Integer id) {
        SolicitudEliminacion s = repoSolicitudesEliminacion.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return Result.CONFLICT;
        s.rechazarSolicitud();
        repoSolicitudesEliminacion.save(s);
        return Result.OK;
    }

    public SolicitudEliminacionDTO crearSolicitud(SolicitudEliminacionDTO dto) {
        SolicitudEliminacion solicitud = new SolicitudEliminacion(dto.getHechoAfectado(), dto.getMotivo());
        this.repoSolicitudesEliminacion.save(solicitud);
        return new SolicitudEliminacionDTO(solicitud);
    }

    // Buscar por id
    public Optional<SolicitudEliminacionDTO> buscarPorId(Integer id) {
        return repoSolicitudesEliminacion.findById(id)
                .map(SolicitudEliminacionDTO::new);
    }


    public List<SolicitudEdicionDTO> obtenerTodasSolicitudesEdicion() {
        return repoSolicitudesEdicion.findAll().stream()
                .map(SolicitudEdicionDTO::new)
                .toList();
    }


    public List<SolicitudEliminacionDTO> obtenerTodasSolicitudesEliminacion(Boolean spam) {
        if (Boolean.TRUE.equals(spam)) { // solo spam
            //Comentado hasta arreglar el repository
//            return repoSolicitudesEliminacion.findAllSolicitudesEliminacionSpam().stream()
//                    .map(SolicitudEliminacionDTO::new)
//                    .toList();
            return new ArrayList<>();
        }
        else if (Boolean.FALSE.equals(spam)) { // todas excepto spam
            return repoSolicitudesEliminacion.findAll().stream()
                    .filter(s -> s.getEstado() != EstadoSolicitud.SPAM)
                    .map(SolicitudEliminacionDTO::new)
                    .toList();
        }
        else { // spam == null → todas
            return repoSolicitudesEliminacion.findAll().stream()
                    .map(SolicitudEliminacionDTO::new)
                    .toList();
        }
    }

    public SolicitudEdicionDTO obtenerSolicitudEdicionPorId(Integer id) {
        Optional<SolicitudEdicion> solicitudOpt = repoSolicitudesEdicion.findById(id);
        return solicitudOpt.map(SolicitudEdicionDTO::new).orElse(null);
    }

    public SolicitudEdicionDTO crearSolicitudEdicion(SolicitudEdicionDTO dto) {
        Hecho hecho = repoHechos.findById(dto.getHechoAfectado())
                .orElseThrow(() -> new IllegalArgumentException("Hecho no encontrado"));
        if (hecho.getFechaCarga().plusDays(7).isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Paso mas de una semana de la carga del Hecho");
        }
        SolicitudEdicion solicitud = new SolicitudEdicion(
                dto.getTituloMod(),
                dto.getDescMod(),
                dto.getCategoriaMod(),
                dto.getLatitudMod(),
                dto.getLongitudMod(),
                dto.getFechaHechoMod(),
                dto.getMultimediaMod(),
                dto.getAnonimidadMod(),
                dto.getSugerencia(),
                dto.getHechoAfectado()
        );
        repoSolicitudesEdicion.save(solicitud);
        return new SolicitudEdicionDTO(solicitud);
    }

    public SolicitudEdicionDTO actualizarEstadoSolicitudEdicion(Integer id, Map<String, Object> requestBody) {
        SolicitudEdicion solicitud = repoSolicitudesEdicion.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
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
        repoSolicitudesEdicion.save(solicitud);
        if (nuevoEstado == EstadoSolicitud.APROBADA) {
            Hecho hecho = repoHechos.findById(solicitud.getHechoAfectado())
                    .orElseThrow(() -> new IllegalArgumentException("Hecho no encontrado"));
            hecho.editarHecho(solicitud.getTituloMod(),
                    solicitud.getDescMod(),
                    solicitud.getCategoriaMod(),
                    solicitud.getLatitudMod(),
                    solicitud.getLongitudMod(),
                    solicitud.getFechaHechoMod(),
                    solicitud.getAnonimidadMod(),
                    solicitud.getMultimediaMod());
            repoHechos.save(hecho);
        }
        return new SolicitudEdicionDTO(solicitud);
    }
}