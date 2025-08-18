package Metamapa.service;
import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.Hechos.Multimedia;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
  public ArrayList<FuenteDeDatos> getFuentesDeDatos() {
    String url = String.format("%s/api-fuentesDeDatos/", baseUrl);
    return restTemplate.getForObject(url, ArrayList.class);
  }

  public Integer cargarHecho(Integer idFuenteDeDatos,
                           String titulo,
                           String descripcion,
                           String categoria,
                           Float latitud,
                           Float longitud,
                           LocalDate fechaHecho,
                           String autor,
                           Boolean anonimo,
                           List<Multimedia> multimedia) {
    String url = String.format("%s/api-fuentesDeDatos/%d/cargarHecho", baseUrl, idFuenteDeDatos);
    Map<String,Object> payload = new HashMap<>();
    payload.put("titulo", titulo);
    if (descripcion != null) payload.put("descripcion", descripcion);
    if (categoria != null) payload.put("categoria", categoria);
    if (latitud != null) payload.put("latitud", latitud);
    if (longitud != null) payload.put("longitud", longitud);
    if (fechaHecho != null) payload.put("fechaHecho", fechaHecho);
    if (autor != null) payload.put("autor", autor);
    payload.put("anonimo", anonimo != null ? anonimo : false);
    // Parsear multimedia a lista

    if (multimedia != null && !multimedia.isEmpty()) {
      List<Map<String,Object>> mm = multimedia.stream()
          .map(mdto -> {
            Map<String,Object> entry = new HashMap<>();        // <-- Map<String,Object>
            entry.put("tipoMultimedia", mdto.getTipoMultimedia().name());
            entry.put("path",           mdto.getPath());
            return entry;
          })
          .collect(Collectors.toList());                      // List<Map<String,Object>>
      payload.put("multimedia", mm);
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String,Object>> request = new HttpEntity<>(payload, headers);

    //restTemplate.postForObject(url, request, Void.class);
    @SuppressWarnings("unchecked")
    Map<String,Object> response = restTemplate.postForObject(
        url,
        request,
        Map.class
    );
    Integer idNum = (Integer) response.get("id");
    return idNum;
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




  public ResponseEntity<String> crearFuente(String jsonPayload) {
    String url = baseUrl + "/api-fuentesDeDatos/";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

    // exchange() te permite capturar status y body exactos de la respuesta
    return restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        String.class
    );
  }
  public Integer crearFuenteYRetornarId(String tipo, String nombre, String url) {
    Map<String,Object> payload = new HashMap<>();
    payload.put("tipo", tipo);
    payload.put("nombre", nombre);
    if (url != null && !url.isBlank()) {
      payload.put("url", url);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String,Object>> request = new HttpEntity<>(payload, headers);

    @SuppressWarnings("unchecked")
    Map<String,Object> response = restTemplate.postForObject(
        baseUrl + "/api-fuentesDeDatos/",
        request,
        Map.class
    );
    Integer idNum = (Integer) response.get("id");
    return idNum;
  }

  }


