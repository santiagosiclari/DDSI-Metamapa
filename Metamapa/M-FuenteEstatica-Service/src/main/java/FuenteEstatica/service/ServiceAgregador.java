package FuenteEstatica.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import java.util.Map;

@Component
public class ServiceAgregador {
  private final RestTemplate rest;
  private final Environment env;
  private final String registryEndpoint;
  private static final int MAX_RETRIES = 5;
  private static final long RETRY_DELAY_MS = 5000;

  public ServiceAgregador(RestTemplate rest,
                          Environment env,
                          @Value("${registro.agregador}") String registryEndpoint) {
    this.rest = rest;
    this.env = env;
    this.registryEndpoint = registryEndpoint;
  }

  @EventListener(WebServerInitializedEvent.class)
  public void onWebServerReady(WebServerInitializedEvent event) {
    int port = event.getWebServer().getPort();
    String host = env.getProperty("server.address", "localhost");

    String scheme = env.getProperty("server.ssl.enabled", "false").equals("true") ? "https" : "http";
    String baseUrl = scheme + "://" + host + ":" + port;
    Map<String, Object> payload = Map.of(
            "url", baseUrl,
            "tipoFuente","Fuente Estatica"
    );

    int retries = 0;
    while (retries < MAX_RETRIES) {
      try {
        rest.postForLocation(registryEndpoint, payload);
        System.out.println("Self-registered in " + registryEndpoint + " -> " + baseUrl);
        return;
      } catch (RestClientException ex) {
        retries++;
        System.err.println("Failed to self-register (Attempt " + retries + "): " + ex.getMessage());
        if (retries < MAX_RETRIES) {
          try {
            Thread.sleep(RETRY_DELAY_MS);  // Esperar 5 segundos antes del próximo intento
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
            break;
          }
        }
      }
    }
    System.err.println("⚠️ Could not register after " + MAX_RETRIES + " attempts.");
  }
}