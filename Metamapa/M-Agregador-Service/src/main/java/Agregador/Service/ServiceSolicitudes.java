package Agregador.Service;
import Agregador.DTO.SolicitudEdicionDTO;
import Agregador.DTO.SolicitudEliminacionDTO;
import Agregador.business.Hechos.Hecho;
import Agregador.business.Solicitudes.*;
import Agregador.persistencia.RepositorioHechos;
import Agregador.persistencia.RepositorioSolicitudes;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

import java.util.Optional;

@Service
public class ServiceSolicitudes {
    public enum Result { OK, NOT_FOUND, CONFLICT, INVALID }
    private final RepositorioSolicitudes repoSolicitudes;
    private final RepositorioHechos repoHechos;

    public ServiceSolicitudes(RepositorioSolicitudes rs, RepositorioHechos rh) {
        this.repoSolicitudes = rs;
        this.repoHechos = rh;
    }

    // @Transactional (opcional si usás BD)
    public Result aprobar(Integer id) {
        SolicitudEliminacion s = repoSolicitudes.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return Result.CONFLICT;
        s.aceptarSolicitud(); // cambia a APROBADA, timestamps, etc.
        //repoAgregador.bloquearHecho(s.getHechoAfectado()); // no mostrar / no re-ingestar
        repoSolicitudes.save(s);
        return Result.OK;
    }

    // @Transactional
    public Result rechazar(Integer id) {
        SolicitudEliminacion s = repoSolicitudes.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return Result.CONFLICT;
        s.rechazarSolicitud();
        repoSolicitudes.save(s);
        return Result.OK;
    }

    public SolicitudEliminacionDTO crearSolicitud(SolicitudEliminacionDTO dto) {
        SolicitudEliminacion solicitud = new SolicitudEliminacion(dto.getHechoAfectado(), dto.getMotivo());
        this.repoSolicitudes.save(solicitud);
        return new SolicitudEliminacionDTO(solicitud);
    }

    // Buscar por id
    public Optional<SolicitudEliminacionDTO> buscarPorId(Integer id) {
        return repoSolicitudes.findById(id)
                .map(SolicitudEliminacionDTO::new);
    }
}

    public List<SolicitudEdicionDTO> obtenerTodasSolicitudesEdicion() {
        return repoSolicitudes.findAllSolicitudesEdicion().stream()
                .map(SolicitudEdicionDTO::new)
                .toList();
    }

    public SolicitudEdicionDTO obtenerSolicitudEdicionPorId(Integer id) {
        Optional<SolicitudEdicion> solicitudOpt = repoSolicitudes.findSolicitudEdicionById(id);
        return solicitudOpt.map(SolicitudEdicionDTO::new).orElse(null);
    }

    public SolicitudEdicionDTO crearSolicitudEdicion(SolicitudEdicionDTO dto) {
        Hecho hecho = repoHechos.findHecho(dto.getHechoAfectado())
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
        repoSolicitudes.save(solicitud);
        return new SolicitudEdicionDTO(solicitud);
    }

    public SolicitudEdicionDTO actualizarEstadoSolicitudEdicion(Integer id, Map<String, Object> requestBody) {
        SolicitudEdicion solicitud = repoSolicitudes.findSolicitudEdicionById(id)
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
        repoSolicitudes.save(solicitud);
        if (nuevoEstado == EstadoSolicitud.APROBADA) {
            Hecho hecho = repoHechos.findHecho(solicitud.getHechoAfectado())
                    .orElseThrow(() -> new IllegalArgumentException("Hecho no encontrado"));
            hecho.editarHecho(solicitud.getTituloMod(),
                    solicitud.getDescMod(),
                    solicitud.getCategoriaMod(),
                    solicitud.getLatitudMod(),
                    solicitud.getLongitudMod(),
                    solicitud.getFechaHechoMod(),
                    solicitud.getAnonimidadMod(),
                    solicitud.getMultimediaMod());
            repoHechos.updateHecho(hecho);
        }
        return new SolicitudEdicionDTO(solicitud);
    }
}