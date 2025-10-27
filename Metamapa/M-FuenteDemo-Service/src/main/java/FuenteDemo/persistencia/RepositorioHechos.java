package FuenteDemo.persistencia;
import FuenteDemo.business.Hechos.Hecho;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioHechos extends JpaRepository<Hecho,Integer>{
}