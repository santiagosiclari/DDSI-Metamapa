package Agregador.persistencia;

import Agregador.business.Consenso.Consenso;
import java.math.BigInteger;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import Agregador.business.Consenso.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioConsenso extends JpaRepository<Consenso, BigInteger>{

}