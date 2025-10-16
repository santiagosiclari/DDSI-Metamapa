package FuenteMetamapa.service;
import FuenteMetamapa.business.FuentesDeDatos.*;
import FuenteMetamapa.business.Hechos.Hecho;
import FuenteMetamapa.persistencia.RepositorioFuentes;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ServiceFuenteMetamapa {
  private final RepositorioFuentes repositorioFuentes;

  public ServiceFuenteMetamapa(RepositorioFuentes repositorioFuentes) {
    this.repositorioFuentes = repositorioFuentes;
  }

  public FuenteMetamapa crearFuente(Map<String, Object> requestBody) {
    String nombre = (String) requestBody.get("nombre");
    String endpoint = (String) requestBody.get("endpoint");
    if (nombre == null || endpoint == null) throw new IllegalArgumentException("Faltan campos para FuenteMetamapa");
    FuenteMetamapa fuenteMetamapa = new FuenteMetamapa(nombre, endpoint);
    repositorioFuentes.agregarFuente(fuenteMetamapa);
    return fuenteMetamapa;
  }

  public List<FuenteMetamapa> getFuentes() {
    return repositorioFuentes.getFuentesDeDatos();
  }

  public FuenteMetamapa obtenerFuente(Integer id) {
    return repositorioFuentes.buscarFuente(id);
  }

  public ArrayList<Hecho> obtenerHechos(Integer id) {
    return obtenerFuente(id).getHechos();
  }
/*
  public void actualizarHechos(Integer id) {
    FuenteMetamapa fuente = obtenerFuente(id);
    if (fuente instanceof FuenteMetamapa fm) {
      fm.actualizarHechos(Collections.emptyMap());
    } else if (fuente instanceof FuenteMetamapa fd) {
      fd.actualizarHechos();
    }
  }
*/

}