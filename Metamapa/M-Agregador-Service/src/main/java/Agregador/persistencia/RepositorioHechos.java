package Agregador.persistencia;
import Agregador.DTO.ConsensoDTO;
import Agregador.DTO.CriterioDTO;
import Agregador.DTO.HechoDTO;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Consenso.Consenso;
import Agregador.business.Hechos.Hecho;
import java.math.BigInteger;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioHechos extends JpaRepository<Hecho, BigInteger>, RepositorioHechosCustom {
  List<Hecho> filtrarPorCriterios(List<Criterio> criterios, Consenso consenso);
  List<Hecho> findByCategoriaIgnoreCaseAndEliminadoFalse(String categoria);
  List<Hecho> findByEliminadoFalse();  // TODO: ver hay que usar en filtrarPorCriterios
}