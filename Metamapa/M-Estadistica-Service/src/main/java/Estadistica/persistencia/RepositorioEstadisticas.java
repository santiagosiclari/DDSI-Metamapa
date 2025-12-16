package Estadistica.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import Estadistica.business.Estadistica.Estadistica;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioEstadisticas extends JpaRepository<Estadistica, Integer>, RepositorioEstadisticasCustom {


}
