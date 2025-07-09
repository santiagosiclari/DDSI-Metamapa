package metemapaSolicitudes.Service;

import domain.business.tiposSolicitudes.SolicitudEdicion;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

@Service
public class ServiceIncidencia {
  private final RestTemplate restTemplate;
  private final String baseUrl;

  public ServiceIncidencia(RestTemplate restTemplate,
                            @Value("${incidencias.service.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public void aplicarEdicionIncidencia(SolicitudEdicion sol) {
    //Creo que se deberia usar el modificar hecho controller

    String url = String.format("%s/api/hechos/%s", baseUrl,sol.getHechoAfectado());
    //Deberia andar bien
    Map<String, Object> updates = new HashMap<>();
    if (sol.getTituloMod()    != null) updates.put("titulo", sol.getTituloMod());
    if (sol.getDescMod()      != null) updates.put("descripcion", sol.getDescMod());
    if (sol.getCategoriaMod() != null) updates.put("categoria", sol.getCategoriaMod());
    if (sol.getUbicacionMod() != null) {
      updates.put("latitud", sol.getUbicacionMod().getLatitud());
      updates.put("longitud", sol.getUbicacionMod().getLongitud());
    }
    if (sol.getFechaHechoMod() != null) updates.put("fechaHecho", sol.getFechaHechoMod());
    if (sol.getAnonimidadMod() != null) updates.put("anonimo", sol.getAnonimidadMod());
    //Recolecta solo los valores actualizados
    HttpHeaders headers = new HttpHeaders();//Armamos encabezados
    headers.setContentType(MediaType.APPLICATION_JSON);//Peticion tipo JSON
    HttpEntity<Map<String, Object>> req = new HttpEntity<>(updates, headers);//Junta to'do para mandarlo junto
    restTemplate.exchange(url, HttpMethod.PATCH, req, Void.class);
  }

/*  public void aplicarEdicionIncidencia(Integer id, Map<String,Object> cambios){
      Hecho hecho = repo.findById(id);
      if (cambios.containsKey("titulo"))    h.setTitulo((String)cambios.get("titulo"));
      if (cambios.containsKey("descripcion")) h.setDescripcion((String)cambios.get("descripcion"));

      repo.save(hecho);
  }*/
}