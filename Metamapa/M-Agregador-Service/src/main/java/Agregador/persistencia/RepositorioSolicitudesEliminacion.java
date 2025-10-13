package Agregador.persistencia;
import Agregador.business.Solicitudes.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface RepositorioSolicitudesEliminacion extends JpaRepository<SolicitudEliminacion, Integer> {
  /*public List<SolicitudEliminacion> findAllSolicitudesEliminacionSpam() {
    return this.findAll().stream()
            .filter(solicitudEliminacion -> solicitudEliminacion.getEstado() == EstadoSolicitud.SPAM)
            .toList();
  }*/
}