package FuenteMetamapa.service;
import FuenteMetamapa.business.FuentesDeDatos.*;
import FuenteMetamapa.business.Hechos.Hecho;
import FuenteMetamapa.persistencia.RepositorioFuentes;
import FuenteMetamapa.persistencia.RepositorioHechos;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ServiceFuenteMetamapa {
  private final RepositorioFuentes repositorioFuentes;
  private final RepositorioHechos repositorioHechos;

  public ServiceFuenteMetamapa(RepositorioFuentes repositorioFuentes, RepositorioHechos repositorioHechos) {
    this.repositorioFuentes = repositorioFuentes;
    this.repositorioHechos = repositorioHechos;
  }

  public FuenteMetamapa crearFuente(Map<String, Object> requestBody) {
    String nombre = (String) requestBody.get("nombre");
    String endpoint = (String) requestBody.get("endpoint");
    if (nombre == null || endpoint == null) throw new IllegalArgumentException("Faltan campos para FuenteMetamapa");
    FuenteMetamapa fuenteMetamapa = new FuenteMetamapa(nombre, endpoint);
    repositorioFuentes.save(fuenteMetamapa);
    return fuenteMetamapa;
  }

  public List<FuenteMetamapa> getFuentes() {
    return repositorioFuentes.findAll();
  }

  public FuenteMetamapa obtenerFuente(Integer id)  {
    return repositorioFuentes.findById(id).orElseThrow(() -> new NoSuchElementException("No existe fuente con ID: " + id ));
  }

  public List<Hecho> obtenerHechos() {
    return repositorioHechos.findAll();
  }

  public ArrayList<Hecho> obtenerHechos(Integer id) {
    return obtenerFuente(id).getHechos();
  }

  public void actualizarHechos(Integer id) {
    FuenteMetamapa fuente = obtenerFuente(id);
    fuente.actualizarHechos(Collections.emptyMap());
  }
}