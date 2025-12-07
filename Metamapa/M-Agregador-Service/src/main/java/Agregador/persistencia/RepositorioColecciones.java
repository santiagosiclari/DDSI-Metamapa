package Agregador.persistencia;
import Agregador.business.Colecciones.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioColecciones extends JpaRepository<Coleccion, UUID>,RepositorioColeccionesCustom{
  List<Criterio> getCriteriosColeccion(UUID id);
  Optional<Coleccion> getColeccion(UUID id);
}