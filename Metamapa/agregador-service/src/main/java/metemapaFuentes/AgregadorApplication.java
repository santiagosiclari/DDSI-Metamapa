package metemapaFuentes;


import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgregadorApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(AgregadorApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "9002"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }
}