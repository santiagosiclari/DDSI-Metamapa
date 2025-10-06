package FuenteDinamica;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class RegistrarmeEnAgregador {
  private final RestTemplate rest;
  private final Environment env;
  private final String registryEndpoint;

  public RegistrarmeEnAgregador(RestTemplate rest,
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
            "url", baseUrl
    );

    try {
      rest.postForLocation(registryEndpoint, payload);
      System.out.println("Self-registered in " + registryEndpoint + " -> " + baseUrl);
    } catch (RestClientException ex) {
      System.err.println("Failed to self-register: " + ex.getMessage());
    }
  }
}