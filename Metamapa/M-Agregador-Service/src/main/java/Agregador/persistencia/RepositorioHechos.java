package Agregador.persistencia;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Consenso.Consenso;
import Agregador.business.Hechos.Hecho;
import java.math.BigInteger;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioHechos extends JpaRepository<Hecho, BigInteger>, RepositorioHechosCustom {
  List<Hecho> filtrarPorCriterios(List<Criterio> criterios, Consenso consenso);
}