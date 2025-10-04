package FuenteDemo;
import FuenteDemo.persistencia.RepositorioFuentes;
import FuenteDemo.service.ServiceFuenteDemo;
import FuenteDemo.web.ControllerFuenteDemo;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FuenteDemoApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(FuenteDemoApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "9006"));
    var context = app.run(args);

    RepositorioFuentes repositorio = new RepositorioFuentes();
    ServiceFuenteDemo service = new ServiceFuenteDemo(repositorio);
    ControllerFuenteDemo controller = new ControllerFuenteDemo(service);


    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}