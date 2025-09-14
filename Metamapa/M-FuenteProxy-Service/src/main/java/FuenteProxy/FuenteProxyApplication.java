package FuenteProxy;

import java.util.Collections;

import FuenteProxy.web.ControllerFuenteProxy;
import FuenteProxy.service.ServiceFuenteProxy;
import FuenteProxy.persistencia.RepositorioFuentes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FuenteProxyApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(FuenteProxyApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "server.port"));
    var context = app.run(args);

    RepositorioFuentes repositorio = new RepositorioFuentes();
    ServiceFuenteProxy service = new ServiceFuenteProxy(repositorio);
    ControllerFuenteProxy controller = new ControllerFuenteProxy(service);


    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}