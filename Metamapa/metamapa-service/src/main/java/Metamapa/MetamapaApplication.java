package Metamapa;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import Metamapa.business.FuentesDeDatos.*; // tus clases
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class MetamapaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetamapaApplication.class, args);
	}

	// 1) Un único ObjectMapper para toda la app
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper()
						.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// Si ya tenés @JsonTypeInfo/@JsonSubTypes en el modelo, no hace falta lo de abajo.
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// Si querés, igual podés registrar subtipos explícitamente:
		mapper.registerSubtypes(
						FuenteDemo.class,
						FuenteDinamica.class,
						FuenteEstatica.class,
						FuenteMetamapa.class,
						FuenteProxy.class
		);
		return mapper;
	}

	// 2) RestTemplate usando ese ObjectMapper (¡y primero en la lista!)
	@Bean
	public RestTemplate restTemplate(ObjectMapper mapper) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpComponentsClientHttpRequestFactory factory =
						new HttpComponentsClientHttpRequestFactory(httpClient);

		RestTemplate rt = new RestTemplate(factory);

		MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
		jackson.setObjectMapper(mapper);

		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(jackson); // nuestro Jackson primero
		for (HttpMessageConverter<?> c : rt.getMessageConverters()) {
			if (!(c instanceof MappingJackson2HttpMessageConverter)) {
				converters.add(c);
			}
		}
		rt.setMessageConverters(converters);
		return rt;
	}
}