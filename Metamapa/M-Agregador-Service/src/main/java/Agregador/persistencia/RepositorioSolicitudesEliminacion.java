package Agregador.persistencia;
import Agregador.business.Solicitudes.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface RepositorioSolicitudesEliminacion extends JpaRepository<SolicitudEliminacion, Integer> {
  List<SolicitudEliminacion> findByEstado(EstadoSolicitud estado);

  @Query("SELECT s FROM SolicitudEliminacion s WHERE s.estado <> :estado")
  List<SolicitudEliminacion> findAllWhereEstadoNot(@Param("estado") EstadoSolicitud estado);
}