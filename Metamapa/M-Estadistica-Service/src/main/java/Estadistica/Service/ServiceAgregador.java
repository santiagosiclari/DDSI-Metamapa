package Estadistica.Service;

import Estadistica.business.Estadistica.Coleccion;
import Estadistica.business.Estadistica.Hecho;
import Estadistica.business.Estadistica.SolicitudEliminacion;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Estadistica.business.Estadistica.Criterios.Criterio;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceAgregador {
  private final RestTemplate restTemplate;

public List<Hecho> getHechosAgregador(String urlBase){
  String url = urlBase + "/api-agregador/hechos";
  ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
          url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
  );
  List<Map<String, Object>> raw = Optional.ofNullable(resp.getBody()).orElseGet(List::of);
  // imprimir raw para debug
  //System.out.println("Hechos raw de la fuente " + urlBase + ": " + raw);

  return raw.stream().map(this::jsonToHecho).toList();

}
  private Hecho jsonToHecho(Map<String,Object> json) {
    BigInteger id = i(json.get("id"));
    String titulo = str(json.get("titulo"));
    String descripcion = str(json.get("descripcion"));
    String categoria = str(json.get("categoria"));
    Float latitud = f(json.get("latitud"));
    Float longitud = f(json.get("longitud"));
    LocalDateTime fechaHecho = date(json.get("fechaHecho"));
    LocalDateTime fechaCarga = date(json.get("fechaCarga"));
    LocalDateTime fechaModificacion = date(json.get("fechaModificacion"));
    Integer perfilId = i(json.get("perfil"));
    Boolean anonimo = bool(json.get("anonimo"));
    Boolean eliminado = bool(json.get("eliminado"));
    Map<String,String> multimedia = (Map<String,String>) json.get("multimedia");
    Map<String,String> metadata = (Map<String,String>) json.get("metadata");
    Integer idFuente = i(json.get("idFuente"));

    Hecho h = new Hecho(
     id,
     titulo,
     descripcion,
     categoria,
     latitud,
     longitud,
     fechaHecho,
     fechaCarga,
     fechaModificacion,
     perfilId,
     anonimo,
     eliminado,
     multimedia,
     metadata,
     idFuente
    );

    return h;
  }

  public Coleccion JsonToColeccion(Map<String, Object> Json)
  {
    String nombre = str(Json.get("nombre"));
    String descripcion = str(Json.get("descripcion"));
    ArrayList<Criterio> criterios = new ArrayList((List<Criterio>) Json.get("criterio"));


    Coleccion c = new Coleccion(
            nombre,
            descripcion,
            criterios
    );
    return c;
  }
  public List<Coleccion> getColeccionesAgregador(String urlBase){
    String url = urlBase + "/api-colecciones/colecciones";
    ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
    );

    List<Map<String, Object>> raw = Optional.ofNullable(resp.getBody()).orElseGet(List::of);

    return (raw.stream().map(json -> JsonToColeccion(json)).toList());

  }

  SolicitudEliminacion JsonToSolicitudesDeEliminacion(Map<String, Object> json)
  {
    //como estado es un objeto en si tal vez no es tan facil, pero la logica es algo asi
      boolean spam = (String)json.get("Estado") == "SPAM";
      
    return new SolicitudEliminacion((String)json.get("Motivo"),
            jsonToHecho((Map<String, Object>)json.get("hechoAfectado")),
            spam);
  }
  public List<SolicitudEliminacion> getSolicitudesEliminacionAgregador(String urlBase){
    String url = urlBase + "/api-solicitudes/solicitudesEliminacion";
    ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
    );

    List<Map<String, Object>> raw = Optional.ofNullable(resp.getBody()).orElseGet(List::of);


    return (raw.stream().map(json -> JsonToSolicitudesDeEliminacion(json)).toList());

  }


  private static String str(Object o)        { return o == null ? null : String.valueOf(o); }
  private static Integer i(Object o)         { try { return o == null ? null : Integer.valueOf(String.valueOf(o)); } catch(Exception e){ return null; } }
  private static BigIntegerI bi(Object o)         { try { return o == null ? null : Integer.valueOf(String.valueOf(o)); } catch(Exception e){ return null; } }
  private static Float f(Object o)           { try { return o == null ? null : Float.valueOf(String.valueOf(o)); } catch(Exception e){ return null; } }
  private static Boolean bool(Object o)      { return o == null ? null : Boolean.valueOf(String.valueOf(o)); }
  private static LocalDateTime date(Object o)    { try { return o == null ? null : LocalDateTime.parse(String.valueOf(o)); } catch(Exception e){ return null; } }


}