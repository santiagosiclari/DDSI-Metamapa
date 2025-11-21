package Usuarios;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UsuariosApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(UsuariosApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "${server.port}"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    //context.close();
  }

}