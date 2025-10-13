package Agregador.persistencia;
import Agregador.business.Colecciones.*;
import org.springframework.stereotype.Repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioColecciones extends JpaRepository<Coleccion, UUID>{
}