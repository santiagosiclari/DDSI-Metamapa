package FuenteDemo.service;
import FuenteDemo.business.FuentesDeDatos.FuenteDemo;
import FuenteDemo.business.Hechos.Hecho;
import FuenteDemo.persistencia.RepositorioFuentes;
import java.util.*;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import FuenteDemo.persistencia.RepositorioHechos;

@Service
public class ServiceFuenteDemo {
  private final RepositorioFuentes repositorioFuentes;
  private final RepositorioHechos repositorioHechos;

  public ServiceFuenteDemo(RepositorioFuentes repositorioFuentes, RepositorioHechos repositorioHechos) {
    this.repositorioFuentes = repositorioFuentes;
    this.repositorioHechos = repositorioHechos;
  }

  public FuenteDemo crearFuente(Map<String, Object> requestBody) {
    String nombre = (String) requestBody.get("nombre");
    String url = (String) requestBody.get("url");
    if (nombre == null || url == null) throw new IllegalArgumentException("Faltan campos para FuenteDemo");
    FuenteDemo fuenteDemo = new FuenteDemo(nombre, url);
    repositorioFuentes.save(fuenteDemo);
    return fuenteDemo;
  }

  public List<FuenteDemo> getFuentes() {
    return repositorioFuentes.findAll();
  }

  public FuenteDemo obtenerFuente(Integer id) {
    return repositorioFuentes.findById(id).orElseThrow(() -> new NoSuchElementException("No existe la fuente"));
  }

  public List<Hecho> obtenerHechos(Integer id) {
    return obtenerFuente(id).getHechos();
  }

  @Scheduled(fixedRate = 30 * 60 * 1000)
  public void actualizarTodosLosHechos(WebServerInitializedEvent event) {
    try {
      List<FuenteDemo> fuentes = repositorioFuentes.findAll();
      fuentes.forEach(FuenteDemo::actualizarHechos);
      repositorioFuentes.saveAll(fuentes);
    }
    catch (Exception e) {
      throw new ExceptionInInitializerError( "Error: No se pudieron actualizar los echos de las fuentes por el siquiente error:" + e);
    }
  }

  public void actualizarHechos(Integer id) {
    FuenteDemo fuente = obtenerFuente(id);
    fuente.actualizarHechos();
  }
}