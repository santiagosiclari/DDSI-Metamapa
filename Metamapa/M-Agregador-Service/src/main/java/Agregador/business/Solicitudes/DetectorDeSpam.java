package Agregador.business.Solicitudes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

@Component
public class DetectorDeSpam {

    @Value("${akismet.api.key}")
    private String apiKey;

    @Value("${akismet.blog.url}")
    private String blogUrl;

    public boolean esSpam(String texto) throws Exception {
        if (texto == null || texto.isBlank()) return false;

        HttpClient client = HttpClient.newHttpClient();

        String requestBody = "blog=" + URLEncoder.encode(blogUrl, StandardCharsets.UTF_8) +
                "&comment_content=" + URLEncoder.encode(texto, StandardCharsets.UTF_8) +
                "&comment_type=contact-form";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + apiKey + ".rest.akismet.com/1.1/comment-check"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Metamapa-App/1.0 | Akismet/1.1")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body().trim().toLowerCase();

        if (!body.equals("true") && !body.equals("false")) {
            throw new RuntimeException("Akismet devolvió un resultado inesperado: " + body);
        }

        return body.equals("true");
    }
}