package FuenteDinamica.persistencia;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  private final ArrayList<FuenteDinamica> fuentesDinamicas = new ArrayList<>();
  private final AtomicInteger seq = new AtomicInteger(1_000_000);

  public List<FuenteDinamica> getFuentesDinamicas() {
    return fuentesDinamicas;
  }

  public FuenteDinamica agregarFuente(FuenteDinamica fuente) {
    if (fuente.getFuenteId() == null) {
      fuente.setFuenteId(seq.getAndIncrement());
    }
    this.fuentesDinamicas.add(fuente);
    return fuente;
  }

  public Optional<FuenteDinamica> buscarFuenteOpt(Integer id) {
    return fuentesDinamicas.stream()
            .filter(f -> Objects.equals(f.getFuenteId(), id))
            .findFirst();
  }

  public FuenteDinamica buscarFuente(Integer id) {
    return buscarFuenteOpt(id).orElseThrow(
            () -> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }
}
