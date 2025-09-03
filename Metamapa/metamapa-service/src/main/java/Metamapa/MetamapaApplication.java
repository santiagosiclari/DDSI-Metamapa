package Metamapa;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@SpringBootApplication
public class MetamapaApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MetamapaApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "${server.port}"));
		app.run(args);
	}

	@Bean
	public RestTemplate restTemplate() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpComponentsClientHttpRequestFactory factory =
						new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(factory);
	}
}