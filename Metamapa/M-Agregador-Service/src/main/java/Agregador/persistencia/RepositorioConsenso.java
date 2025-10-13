package Agregador.persistencia;
import Agregador.business.Consenso.Consenso;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioConsenso extends JpaRepository<Consenso, BigInteger> {
  Optional<Consenso> findByDescripcion(String descripcion);
}