package Usuarios;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UsuariosApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(UsuariosApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "${server.port}"));
    var context = app.run(args);
  }
}