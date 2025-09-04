package Agregador.Service;
import Agregador.DTO.SolicitudEliminacionDTO;
import Agregador.business.Solicitudes.*;
import Agregador.persistencia.RepositorioAgregador;
import Agregador.persistencia.RepositorioSolicitudes;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceSolicitudes {
    public enum Result { OK, NOT_FOUND, CONFLICT, INVALID }
    private final RepositorioSolicitudes repoSolicitudes;

    public ServiceSolicitudes(RepositorioSolicitudes rs) {
        this.repoSolicitudes = rs;
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

