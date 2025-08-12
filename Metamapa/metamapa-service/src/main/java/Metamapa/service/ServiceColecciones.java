package Metamapa.service;

import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.criterio.Coleccion;
import Metamapa.business.criterio.Criterio;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceColecciones {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceColecciones(RestTemplate restTemplate,
                            @Value("${colecciones.service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public ArrayList<Coleccion> getColecciones() {
    String url = String.format("%s/api-colecciones/", baseUrl);
    return restTemplate.getForObject(url, ArrayList.class);
  }

  public Coleccion getColeccion(UUID uuid) {
    String url = String.format("%s/api-colecciones/%d", baseUrl, uuid);
    return restTemplate.getForObject(url, Coleccion.class);
  }
/*
  public Coleccion(String titulo, String desc, ArrayList<Criterio> pertenencia, ArrayList<Criterio> noPertenencia){
    this.titulo=titulo;
    this.descripcion = desc;
    this.criterioPertenencia = pertenencia;
    this.criterioNoPertenencia = noPertenencia;
    //this.agregador=agregador;
    this.handle = UUID.randomUUID();
  }
*/
  public UUID crearColeccion(String titulo,String descripcion, ArrayList<Criterio> pertenencia, ArrayList<Criterio>noPertenencia ) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("titulo", titulo);
    payload.put("descripcion", descripcion);
    payload.put("pertenencia", pertenencia);
    payload.put("noPertenencia", noPertenencia);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String,Object>> request = new HttpEntity<>(payload, headers);

    @SuppressWarnings("unchecked")
    Map<String,Object> response = restTemplate.postForObject(
        baseUrl + "/api-fuentesDeDatos/",
        request,
        Map.class
    );
    UUID handle = (UUID) response.get("handle");
    return handle;
  }
}