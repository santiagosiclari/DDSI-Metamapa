package Agregador;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@SpringBootApplication
@EnableScheduling
public class AgregadorApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(AgregadorApplication.class);
    // si querés fijar el puerto por código (mejor en application.properties):
    app.setDefaultProperties(Collections.singletonMap("server.port", "9004"));
    app.run(args); // <-- ¡una sola vez!


  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public MeterRegistry meterRegistry() {
    return new SimpleMeterRegistry();
  }
}
