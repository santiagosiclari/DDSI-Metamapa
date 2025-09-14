package FuenteProxy.service;
import FuenteProxy.business.FuentesDeDatos.*;
import FuenteProxy.business.Hechos.Hecho;
import FuenteProxy.persistencia.RepositorioFuentes;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ServiceFuenteProxy {
  private final RepositorioFuentes repositorioFuentes;

  public ServiceFuenteProxy(RepositorioFuentes repositorioFuentes) {
    this.repositorioFuentes = repositorioFuentes;
  }

  public FuenteProxy crearFuente(Map<String, Object> requestBody) {
    String tipo = (String) requestBody.get("tipo");
    if (tipo == null) throw new IllegalArgumentException("Falta el campo 'tipo'");
    switch (tipo) {
      case "FuenteDemo": {
        String nombre = (String) requestBody.get("nombre");
        String url = (String) requestBody.get("url");
        if (nombre == null || url == null) throw new IllegalArgumentException("Faltan campos para FuenteDemo");
        FuenteDemo fuenteDemo = new FuenteDemo(nombre, url);
        repositorioFuentes.agregarFuente(fuenteDemo);
        return fuenteDemo;
      }
      case "FuenteMetamapa": {
        String nombre = (String) requestBody.get("nombre");
        String endpoint = (String) requestBody.get("endpoint");
        if (nombre == null || endpoint == null) throw new IllegalArgumentException("Faltan campos para FuenteMetamapa");
        FuenteMetamapa fuenteMetamapa = new FuenteMetamapa(nombre, endpoint);
        repositorioFuentes.agregarFuente(fuenteMetamapa);
        return fuenteMetamapa;
      }
      default:
        throw new IllegalArgumentException("Tipo de fuente inválido: " + tipo);
    }
  }

  public List<FuenteProxy> getFuentes() {
    return new ArrayList<>(repositorioFuentes.fuentesDeDatos);
  }

  public FuenteProxy obtenerFuente(Integer id) {
    return repositorioFuentes.buscarFuente(id);
  }

  public List<Hecho> obtenerHechos(Integer id) {
    return obtenerFuente(id).getHechos();
  }

  public void actualizarHechos(Integer id) {
    FuenteProxy fuente = obtenerFuente(id);
    if (fuente instanceof FuenteMetamapa fm) {
      fm.actualizarHechos(Collections.emptyMap());
    } else if (fuente instanceof FuenteDemo fd) {
      fd.actualizarHechos();
    }
  }


}