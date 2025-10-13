package Agregador.persistencia;
import Agregador.business.Hechos.Hecho;
import Agregador.business.Solicitudes.*;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface RepositorioSolicitudesEdicion extends JpaRepository<SolicitudEdicion, Integer> {

}