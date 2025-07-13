package metemapaFuentes;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FuentesDeDatosApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(FuentesDeDatosApplication.class);
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