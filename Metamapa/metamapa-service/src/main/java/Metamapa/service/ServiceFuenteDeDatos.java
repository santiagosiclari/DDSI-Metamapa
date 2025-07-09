package Metamapa.service;
import domain.business.FuentesDeDatos.FuenteDeDatos;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

@Service
public class ServiceFuenteDeDatos {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceFuenteDeDatos(RestTemplate restTemplate,
                             @Value("${fuentes.service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public FuenteDeDatos getFuenteDeDatos(Integer idFuenteDeDatos) {
    String url = String.format("%s/api-fuentesDeDatos/%d", baseUrl, idFuenteDeDatos);
    return restTemplate.getForObject(url, FuenteDeDatos.class);
  }

  public void cargarCSV(Integer idFuenteDeDatos,MultipartFile file)throws IOException {
    String url = String.format("%s/api-fuentesDeDatos/%d/cargarCSV", baseUrl, idFuenteDeDatos);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    InputStreamResource resource = new InputStreamResource(file.getInputStream()){
      @Override
      public String getFilename() {
        return file.getOriginalFilename();
      }
      @Override
      public long contentLength() {
        return file.getSize();
      }
    };
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", resource);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    restTemplate.postForObject(url, requestEntity, Void.class);
  }


}