package Estadistica.persistencia;
import Estadistica.business.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoroHechos extends JpaRepository<Hecho, Integer> {

}
