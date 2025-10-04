package FuenteDemo.service;

import FuenteDemo.business.FuentesDeDatos.FuenteDemo;
import FuenteDemo.business.FuentesDeDatos.FuenteProxy;
import FuenteDemo.business.Hechos.Hecho;
import FuenteDemo.persistencia.RepositorioFuentes;
import java.util.*;
import org.springframework.stereotype.Service;
//TODO Pasar esto al repositorio, creo que no va aca
@Service
public class ServiceFuenteDemo {
  private final RepositorioFuentes repositorioFuentes;

  public ServiceFuenteDemo(RepositorioFuentes repositorioFuentes) {
    this.repositorioFuentes = repositorioFuentes;
  }

  public FuenteDemo crearFuente(Map<String, Object> requestBody) {
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
      default:
        throw new IllegalArgumentException("Tipo de fuente inválido: " + tipo);
    }
  }

  public List<FuenteDemo> getFuentes() {
    return new ArrayList<>(repositorioFuentes.getFuentesDeDatos());
  }

  public FuenteDemo obtenerFuente(Integer id) {
    return repositorioFuentes.buscarFuente(id);
  }

  public List<Hecho> obtenerHechos(Integer id) {
    return obtenerFuente(id).getHechos();
  }

  /*public void actualizarHechos(Integer id) {
    FuenteProxy fuente = obtenerFuente(id);
    if (fuente instanceof FuenteMetamapa fm) {
      fm.actualizarHechos(Collections.emptyMap());
    } else if (fuente instanceof FuenteDemo fd) {
      fd.actualizarHechos();
    }
  }*/


}