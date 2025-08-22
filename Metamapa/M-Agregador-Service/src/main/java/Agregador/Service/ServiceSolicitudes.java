package Agregador.Service;
import Agregador.DTO.SolicitudEliminacionDTO;
import Agregador.business.Solicitudes.EstadoSolicitud;
import Agregador.business.Solicitudes.SolicitudEliminacion;
import Agregador.persistencia.RepositorioAgregador;
import org.springframework.stereotype.Service;

@Service
public class ServiceSolicitudes {
    public enum Result { OK, NOT_FOUND, CONFLICT, INVALID }
    private final RepositorioAgregador repositorioAgregador;

    public ServiceSolicitudes(RepositorioAgregador ra) {
        this.repositorioAgregador = ra;
    }

    // @Transactional (opcional si usás BD)
    public Result aprobar(Integer id) {
        var s = repositorioAgregador.findSolicitudById(id);
        if (s == null) return Result.NOT_FOUND;
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return Result.CONFLICT;

        s.aceptarSolicitud(); // cambia a APROBADA, timestamps, etc.
        //repoAgregador.bloquearHecho(s.getHechoAfectado()); // no mostrar / no re-ingestar
        //repositorioAgregador.save(s);
        return Result.OK;
    }

    // @Transactional
    public Result rechazar(Integer id) {
        var s = repositorioAgregador.findSolicitudById(id);
        if (s == null) return Result.NOT_FOUND;
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) return Result.CONFLICT;

        s.rechazarSolicitud();
        //repositorioAgregador.save(s);
        return Result.OK;
    }

    public SolicitudEliminacionDTO crearSolicitud(SolicitudEliminacionDTO dto) {
        SolicitudEliminacion solicitud = new SolicitudEliminacion(dto.getHechoAfectado(), dto.getMotivo());
        //repositorioAgregador.save(solicitud); TODO: guardar en la base de datos
        return new SolicitudEliminacionDTO(solicitud);
    }
}

