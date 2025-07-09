package metemapaAgregador;
import java.util.Collections;
import metemapaAgregador.Service.ServiceFuenteDeDatos;
import metemapaAgregador.web.ControllerAgregador;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.*;
import java.util.ArrayList;

@SpringBootApplication
public class AgregadorApplication {


  static ControllerAgregador controler = new ControllerAgregador();

  private static void scheduleActualizacion()
  {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.scheduleAtFixedRate(() -> controler.actualizarHechos(), 0, 2, TimeUnit.HOURS);
  }

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(AgregadorApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "server.port"));
    var context = app.run(args);

    scheduleActualizacion();
    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();


  }


  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}