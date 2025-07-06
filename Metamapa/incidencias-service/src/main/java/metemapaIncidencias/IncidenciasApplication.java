package metemapaIncidencias;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IncidenciasApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(IncidenciasApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "${server.port}"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }
}