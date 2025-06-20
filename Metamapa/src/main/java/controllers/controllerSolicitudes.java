package controllers;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class controllerSolicitudes {
  public static void main(String[] args) {
    //SpringApplication.run(testApplication.class, args);
    SpringApplication app = new SpringApplication(demo.testApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8082"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    app.run(args);
  }

  @GetMapping("/solicitudesEliminacion")
  public String solicitudesEliminacion(@RequestParam(value = "nombre", defaultValue = "") String nombre, @RequestParam(value = "Estado",defaultValue = "Pendiente") String estado) {

  }
}
