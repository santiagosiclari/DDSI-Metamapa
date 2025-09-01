package Agregador;
import java.util.Collections;
import Agregador.Service.ServiceFuenteDeDatos;
import Agregador.persistencia.RepositorioColecciones;
import Agregador.persistencia.RepositorioHechos;
import Agregador.web.ControllerAgregador;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.*;

@SpringBootApplication
public class AgregadorApplication {
  static RepositorioHechos repositorioHechos;
  static ServiceFuenteDeDatos serviceFuenteDeDatos = new ServiceFuenteDeDatos(new RestTemplate(),"${fuentes.service.url}", repositorioHechos);
  static ControllerAgregador controllerAgregador = new ControllerAgregador(serviceFuenteDeDatos, new RepositorioColecciones());

  private static void scheduleActualizacion() {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(() -> controllerAgregador.actualizarHechos(), 0, 1, TimeUnit.HOURS);
  }

  private static final MeterRegistry registry = new SimpleMeterRegistry();

  private static void scheduleConsensuar() {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Double requests = registry.find("http.server.requests.count").counter() != null
        ? registry.find("http.server.requests.count").counter().count()
        : 0.0;
    if (requests < 100) { // umbral de bajo tráfico en el último minuto
      scheduler.scheduleAtFixedRate(() -> controllerAgregador.consensuarHechos(), 0, 30, TimeUnit.MINUTES);
    }
  }

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(AgregadorApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "server.port"));
    var context = app.run(args);

    scheduleActualizacion();
    scheduleConsensuar();
    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}