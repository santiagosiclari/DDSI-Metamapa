package FuenteMetamapa.persistencia;
import FuenteMetamapa.business.FuentesDeDatos.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  public ArrayList<FuenteMetamapa> fuentesDeDatos = new ArrayList<>();

  public List<FuenteMetamapa> getFuentesDeDatos() {
    return fuentesDeDatos;
  }

  public void agregarFuente(FuenteMetamapa fuente) {
    this.fuentesDeDatos.add(fuente);
  }

  public FuenteMetamapa buscarFuente(Integer id) {
    return fuentesDeDatos.stream().filter(f-> Objects.equals(f.getId(), id)).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }
}