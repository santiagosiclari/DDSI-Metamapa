package Agregador.persistencia;
import Agregador.business.Solicitudes.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioSolicitudesEdicion extends JpaRepository<SolicitudEdicion, Integer> {
}