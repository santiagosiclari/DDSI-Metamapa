package FuenteMetamapa;
import java.util.Collections;
import FuenteMetamapa.web.ControllerFuenteMetamapa;
import FuenteMetamapa.service.ServiceFuenteMetamapa;
import FuenteMetamapa.persistencia.RepositorioFuentes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FuenteMetamapaApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(FuenteMetamapaApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "server.port"));
    var context = app.run(args);

    RepositorioFuentes repositorio = new RepositorioFuentes();
    ServiceFuenteMetamapa service = new ServiceFuenteMetamapa(repositorio);
    ControllerFuenteMetamapa controller = new ControllerFuenteMetamapa(service);


    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}