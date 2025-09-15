package Agregador.business.deprecado.FuentesDeDatos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import Agregador.business.deprecado.incidencias.Hecho;
import Agregador.business.Solicitudes.SolicitudEliminacion;
import org.springframework.web.client.RestTemplate;

@JsonTypeName("FUENTEMETAMAPA")
public class FuenteMetamapa extends FuenteProxy {
  final private RestTemplate restTemplate;
  static private Integer contadorID = 1;

  public FuenteMetamapa(String nombre, String endpointBase) {
    super(nombre,endpointBase);
    this.id = contadorID++;
    this.nombre = nombre;
    this.hechos = new ArrayList<>();
    this.restTemplate = new RestTemplate();
    this.tipoFuente = tipoFuente.FUENTEMETAMAPA;
  }
  public void actualizarHechos(Map<String, String> filtros) {
    try {
      String url = getEndpointBase() + "/hechos" + construirQuery(filtros);
      List<Hecho> nuevosHechos = obtenerHechosDesdeURL(url);
      actualizarLista(nuevosHechos);
    } catch (Exception e) {
      System.err.println("Error al actualizar hechos: " + e.getMessage());
    }
  }

  public void actualizarHechosDeColeccion(String idColeccion, Map<String, String> filtros) {
    try {
      String url = getEndpointBase() + "/colecciones/" + idColeccion + "/hechos" + construirQuery(filtros);
      List<Hecho> nuevosHechos = obtenerHechosDesdeURL(url);
      actualizarLista(nuevosHechos);
    } catch (Exception e) {
      System.err.println("Error al actualizar hechos de colección: " + e.getMessage());
    }
  }

  private String construirQuery(Map<String, String> filtros) {
    if (filtros == null || filtros.isEmpty()) return "";
    return "?" + filtros.entrySet().stream()
            .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
  }

  private List<Hecho> obtenerHechosDesdeURL(String url) {
    try {
      String json = restTemplate.getForObject(url, String.class);
      return parser.parsearHechos(json, this.getId());
    } catch (Exception e) {
      System.err.println("Error obteniendo hechos desde URL: " + e.getMessage());
      return Collections.emptyList();
    }
  }

  private void actualizarLista(List<Hecho> nuevosHechos) {
    for (Hecho h : nuevosHechos) {
      boolean yaExiste = this.hechos.stream()
              .anyMatch(e -> e.getTitulo().equalsIgnoreCase(h.getTitulo()));
      if (!yaExiste) {
        this.hechos.add(h);
      }
    }
  }

  public void solicitarEliminacion(SolicitudEliminacion solicitud) {
    try {
      restTemplate.postForEntity(getEndpointBase() + "/solicitudes", solicitud, Void.class);
    } catch (Exception e) {
      System.err.println("Error al enviar solicitud de eliminación: " + e.getMessage());
    }
  }
}