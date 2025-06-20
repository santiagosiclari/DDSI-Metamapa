package controllers;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class controllerIncidencias {
  public static void main(String[] args) {
    //SpringApplication.run(testApplication.class, args);
    SpringApplication app = new SpringApplication(demo.testApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    app.run(args);
  }

  @GetMapping("/")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name,@RequestParam(value = "valor2", defaultValue = "boca") String valor2) {
    return String.format("Hello %s! sos de %s?", name, valor2);
  }
}
