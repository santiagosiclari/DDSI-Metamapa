package FuenteMetamapa.business.FuentesDeDatos;
import FuenteMetamapa.DTO.SolicitudEliminacionDTO;
import FuenteMetamapa.business.Hechos.Hecho;
import FuenteMetamapa.business.Parsers.JSONHechoParser;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import java.net.URLEncoder;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.*;
import org.springframework.web.client.RestTemplate;

@JsonTypeName("FUENTEMETAMAPA")
@Getter @Setter
@Entity
public class FuenteMetamapa {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuentesContador")
  @SequenceGenerator(name = "fuentesContador", sequenceName = "fuentesContador", initialValue = 4000000, allocationSize = 1)
  protected Integer id;
  public String endpointBase;
  public String nombre;
  public ArrayList<Hecho> hechos;
  private LocalDateTime fechaUltimaConsulta;
  @Transient
  final private RestTemplate restTemplate;
  //static private Integer contadorID = 4000000;

  public FuenteMetamapa() {
    this.restTemplate = new RestTemplate();
  }

  public FuenteMetamapa(String nombre, String endpointBase) {
    this.nombre = nombre;
    this.endpointBase = endpointBase;
    this.nombre = nombre;
    this.hechos = new ArrayList<>();
    this.restTemplate = new RestTemplate();
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
      JSONHechoParser parser = new JSONHechoParser();
      return parser.parsearHechos(json, this);
    } catch (Exception e) {
      System.err.println("Error obteniendo hechos desde URL: " + e.getMessage());
      return Collections.emptyList();
    }
  }

  private Map<String, Object> siguienteHecho(String UrlBase,LocalDateTime fechaUltimaConsulta) {
    RestTemplate rest = new RestTemplate();

    return rest.getForObject(UrlBase + "?fechaReporteDesdeP=" + fechaUltimaConsulta,Map.class);
  }


  private void actualizarLista(List<Hecho> nuevosHechos) {
    Map<String, Object> datos = siguienteHecho(this.endpointBase,this.getFechaUltimaConsulta());
    while (datos != null) {
      Hecho nuevoHecho = new Hecho(
          (String) datos.get("titulo"),
          (String) datos.get("descripcion"),
          (String) datos.get("categoria"),
          (Float) datos.get("latitud"),
          (Float) datos.get("longitud"),
          (LocalDate) datos.get("fechaHecho"),
          this
      );

      boolean yaExiste = hechos.stream()
          .anyMatch(e -> e.getTitulo().equalsIgnoreCase(nuevoHecho.getTitulo()));

      if (!yaExiste)
        hechos.add(nuevoHecho);

      if (nuevoHecho.getFechaHecho() != null) {
        LocalDateTime fechaHecho = nuevoHecho.getFechaHecho().atStartOfDay();
        if (fechaHecho.isAfter(fechaUltimaConsulta)) {
          fechaUltimaConsulta = fechaHecho;
        }
      } else {
        fechaUltimaConsulta = LocalDateTime.now(ZoneId.of("UTC"));
      }
      datos = siguienteHecho(this.endpointBase,fechaUltimaConsulta);
    }
  }

  public void solicitarEliminacion(SolicitudEliminacionDTO solicitud) {
    try {
      restTemplate.postForEntity(getEndpointBase() + "/solicitudes", solicitud, Void.class);
    } catch (Exception e) {
      System.err.println("Error al enviar solicitud de eliminación: " + e.getMessage());
    }
  }
}