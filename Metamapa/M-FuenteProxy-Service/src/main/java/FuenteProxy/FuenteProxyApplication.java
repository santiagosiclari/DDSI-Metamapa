package java.FuenteProxy;

import java.util.Collections;

import java.FuenteProxy.web.ControllerFuenteProxy;
import java.FuenteProxy.service.ServiceFuenteProxy;
import java.FuenteProxy.persistencia.RepositorioFuentes;
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


    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}