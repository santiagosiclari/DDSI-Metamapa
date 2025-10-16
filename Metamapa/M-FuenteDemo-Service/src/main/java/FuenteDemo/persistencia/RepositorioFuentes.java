package FuenteDemo.persistencia;
import FuenteDemo.business.FuentesDeDatos.FuenteDemo;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  public ArrayList<FuenteDemo> fuentesDeDatos = new ArrayList<>();

  public List<FuenteDemo> getFuentesDeDatos() {
    return fuentesDeDatos;
  }

  public void agregarFuente(FuenteDemo fuente) {
    this.fuentesDeDatos.add(fuente);
  }

  public FuenteDemo buscarFuente(Integer id) {
    return fuentesDeDatos.stream().filter(f-> Objects.equals(f.getId(), id)).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }
}