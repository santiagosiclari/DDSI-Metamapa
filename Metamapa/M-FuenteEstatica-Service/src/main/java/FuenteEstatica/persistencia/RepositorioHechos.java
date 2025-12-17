package FuenteEstatica.persistencia;
import FuenteEstatica.business.Hechos.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioHechos {
  @Value("${rutas.pendientes}")
  private String rutaPending;
  @Value("${rutas.procesados}")
  private String rutaProcessed;

  private final ArrayList<Hecho> hechos = new ArrayList<>();

  public void save(Hecho h) {
    hechos.add(h);
  }

  public Hecho findById(int id) {
    return (hechos.stream().filter(h -> h.getId() == id).findFirst()).get();
  }

  public void modificarHecho(Hecho hecho) {
  }
}