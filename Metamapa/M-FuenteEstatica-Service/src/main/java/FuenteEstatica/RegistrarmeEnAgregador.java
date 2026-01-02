package FuenteEstatica;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class RegistrarmeEnAgregador {
  private final RestTemplate rest;
  private final Environment env;
  private final String registryEndpoint;
  private static final int MAX_RETRIES = 15;
  private static final long RETRY_DELAY_MS = 10000;

  public RegistrarmeEnAgregador(RestTemplate rest,
                                Environment env,
                                @Value("${registro.agregador}") String registryEndpoint) {
    this.rest = rest;
    this.env = env;
    this.registryEndpoint = registryEndpoint;
  }

  @EventListener(WebServerInitializedEvent.class)
  public void onWebServerReady(WebServerInitializedEvent event) {
    Thread registrationThread = new Thread(() -> {
      int port = event.getWebServer().getPort();
      String host = env.getProperty("registration.hostname", "fuente-estatica");
      String baseUrl = "http://" + host + ":" + port + "/api-fuentesDeDatos";

      Map<String, Object> payload = Map.of("url", baseUrl, "tipoFuente", "Fuente Estatica");

      int intentos = 0;
      while (intentos < 100) { // Subimos a 100 intentos
        try {
          rest.postForLocation(registryEndpoint, payload);
          System.out.println("✅ REGISTRO EXITOSO en intento " + (intentos + 1));
          return; // Salimos del hilo si funcionó
        } catch (Exception ex) {
          intentos++;
          System.err.println("⏳ Esperando al Agregador (Intento " + intentos + "/100)...");
          try {
            Thread.sleep(10000); // 10 segundos
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    });
    registrationThread.setName("Retry-Registration-Thread");
    registrationThread.start();
  }
}