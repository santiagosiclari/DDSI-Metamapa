package Agregador.persistencia;
import Agregador.business.Agregador.Agregador;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.Hecho;
import org.springframework.stereotype.Repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioColecciones extends JpaRepository<Coleccion, UUID>{

}