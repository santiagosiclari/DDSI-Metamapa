package Agregador.persistencia;
import Agregador.business.Hechos.Hecho;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioHechos extends JpaRepository<Hecho, BigInteger>, RepositorioHechosCustom {
  List<Hecho> findByCategoriaIgnoreCaseAndEliminadoFalse(String categoria);
}