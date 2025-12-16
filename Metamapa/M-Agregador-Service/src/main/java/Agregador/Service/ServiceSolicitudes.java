package Agregador.Service;
import Agregador.DTO.*;
import Agregador.business.Hechos.Hecho;
import Agregador.business.Solicitudes.*;
import Agregador.persistencia.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceSolicitudes {
    public enum Result { OK, NOT_FOUND, CONFLICT, INVALID }
    private final RepositorioSolicitudesEliminacion repoSolicitudesEliminacion;
    private final RepositorioSolicitudesEdicion repoSolicitudesEdicion;
    private final RepositorioHechos repoHechos;

    @Transactional
    public Result aprobar(Integer id) {
        SolicitudEliminacion s = repoSolicitudesEliminacion.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud de eliminación no encontrada con id " + id));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return Result.CONFLICT;
        s.aceptarSolicitud(); // cambia a APROBADA, timestamps, etc.
        repoSolicitudesEliminacion.save(s);
        return Result.OK;
    }

    @Transactional
    public Result rechazar(Integer id) {
        SolicitudEliminacion s = repoSolicitudesEliminacion.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud de eliminación no encontrada con id " + id));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return Result.CONFLICT;
        s.rechazarSolicitud();
        repoSolicitudesEliminacion.save(s);
        return Result.OK;
    }

    public SolicitudEliminacionDTO crearSolicitud(SolicitudEliminacionDTO dto) {
        Hecho hecho = repoHechos.findById(dto.getHechoAfectado())
                .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado"));
        SolicitudEliminacion solicitud = new SolicitudEliminacion(hecho, dto.getMotivo());
        this.repoSolicitudesEliminacion.save(solicitud);
        return new SolicitudEliminacionDTO(solicitud);
    }

    // Buscar por id
    public SolicitudEliminacionDTO buscarPorId(Integer id) {
        SolicitudEliminacion s = repoSolicitudesEliminacion.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud de eliminación no encontrada con id " + id));
        return new SolicitudEliminacionDTO(s);
    }

    public List<SolicitudEliminacionDTO> obtenerTodasSolicitudesEliminacion(Boolean spam) {
        if (Boolean.TRUE.equals(spam)) { // solo spam
            return toDTOs(repoSolicitudesEliminacion.findByEstado(EstadoSolicitud.SPAM));
        } else if (Boolean.FALSE.equals(spam)) {
            return toDTOs(repoSolicitudesEliminacion.findAllWhereEstadoNot(EstadoSolicitud.SPAM));
        } else { // todos
            return toDTOs(repoSolicitudesEliminacion.findAll());
        }
    }


    public List<SolicitudEdicionDTO> obtenerTodasSolicitudesEdicion() {
        return repoSolicitudesEdicion.findAll().stream()
                .map(SolicitudEdicionDTO::new)
                .toList();
    }

    private List<SolicitudEliminacionDTO> toDTOs(List<SolicitudEliminacion> entidades) {
        return entidades.stream()
                .map(SolicitudEliminacionDTO::new)
                .toList();
    }

    public SolicitudEdicionDTO obtenerSolicitudEdicionPorId(Integer id) {
        SolicitudEdicion s = repoSolicitudesEdicion.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud de edicion no encontrada con id " + id));
        return new SolicitudEdicionDTO(s);
    }

    public SolicitudEdicionDTO crearSolicitudEdicion(SolicitudEdicionDTO dto) {
        Hecho hecho = repoHechos.findById(dto.getHechoAfectado())
                .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado"));
        if (hecho.getFechaCarga().plusDays(7).isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Paso mas de una semana de la carga del Hecho");
        SolicitudEdicion solicitud = new SolicitudEdicion(
                dto.getTituloMod(),
                dto.getDescMod(),
                dto.getCategoriaMod(),
                dto.getLatitudMod(),
                dto.getLongitudMod(),
                dto.getFechaHechoMod(),
                dto.getAnonimidadMod(),
                dto.getSugerencia(),
                hecho
        );
        repoSolicitudesEdicion.save(solicitud);
        return new SolicitudEdicionDTO(solicitud);
    }

    @Transactional
    public SolicitudEdicionDTO actualizarEstadoSolicitudEdicion(Integer id, Map<String, Object> requestBody) {
        SolicitudEdicion solicitud = repoSolicitudesEdicion.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud de edición no encontrada con id " + id));
        String estadoStr = Optional.ofNullable(requestBody.get("estado"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(String::trim)
                .map(String::toUpperCase)
                .orElseThrow(() -> new IllegalArgumentException("Campo 'estado' requerido y debe ser un string no vacío"));
        EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(estadoStr);
        if (nuevoEstado == EstadoSolicitud.APROBADA) {
            solicitud.aceptarSolicitud();
        } else if (nuevoEstado == EstadoSolicitud.RECHAZADA) {
            solicitud.rechazarSolicitud();
        } else {
            throw new IllegalArgumentException("Solo se permite cambiar a estado APROBADA o RECHAZADA");
        }
        repoSolicitudesEdicion.save(solicitud);
        return new SolicitudEdicionDTO(solicitud);
    }
}