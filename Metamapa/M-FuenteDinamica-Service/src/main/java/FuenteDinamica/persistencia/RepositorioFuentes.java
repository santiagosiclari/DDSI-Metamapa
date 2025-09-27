package FuenteDinamica.persistencia;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioFuentes extends JpaRepository<FuenteDinamica, Integer> {
  /*private final ArrayList<FuenteDinamica> fuentesDinamicas = new ArrayList<>();
  private final AtomicInteger seq = new AtomicInteger(1_000_000);

  public List<FuenteDinamica> findAll() {
    return fuentesDinamicas;
  }

  public FuenteDinamica save(FuenteDinamica fuente) {
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

  public Optional<FuenteDinamica> findById(Integer id) {
    return buscarFuenteOpt(id);
  }*/
}