package FuenteDinamica.persistencia;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import org.springframework.stereotype.Repository;

// FuenteDinamica.persistencia.RepositorioFuentes
@Repository
public class RepositorioFuentes {
  @Getter
  private final List<FuenteDinamica> fuentesDinamicas = new ArrayList<>();
  private final AtomicInteger seq = new AtomicInteger(1_000_000);

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

  public List<FuenteDinamica> listar() { return List.copyOf(fuentesDinamicas); }
}
