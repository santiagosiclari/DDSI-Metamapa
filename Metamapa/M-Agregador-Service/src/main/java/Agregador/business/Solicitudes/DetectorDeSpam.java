package Agregador.business.Solicitudes;
import java.net.http.*;
import java.net.URI;

public class DetectorDeSpam {
    private static final String API_KEY = "401e1369cc60";
    private static final String BLOG_URL = "http://localhost:9004";

    static boolean esSpam(String texto) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + API_KEY + ".rest.akismet.com/1.1/comment-check"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "blog=" + BLOG_URL +
                        "&comment_content=" + texto
//                    +
//                    "&comment_author=Spammer" +
//                    "&comment_author_email=spammer@example.com"
                ))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body().trim();
        if (!body.equalsIgnoreCase("true") && !body.equalsIgnoreCase("false"))
            throw new RuntimeException("Akismet devolvió un resultado inválido: " + body);
        return Boolean.parseBoolean(response.body());
    }
}