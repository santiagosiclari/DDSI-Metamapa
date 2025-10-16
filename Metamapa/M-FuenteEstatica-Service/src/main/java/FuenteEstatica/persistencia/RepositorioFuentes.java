package FuenteEstatica.persistencia;
import FuenteEstatica.business.FuentesDeDatos.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  public ArrayList<FuenteEstatica> fuentesDeDatos = new ArrayList<>();

  public void agregarFuente(FuenteEstatica fuente) {
    this.fuentesDeDatos.add(fuente);
  }
  public RepositorioFuentes() {
  }

  public FuenteEstatica buscarFuente(Integer id) {
    return fuentesDeDatos.stream().filter(f-> Objects.equals(f.getFuenteId(), id)).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }

  public List<FuenteEstatica> getFuentesDeDatos() {
    return fuentesDeDatos;
  }
}