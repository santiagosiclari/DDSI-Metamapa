package domain.business.FuentesDeDatos;
import com.fasterxml.jackson.annotation.JsonTypeName;
import domain.business.Parsers.HechoParser;
import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import domain.business.externo.demo.Conexion;
import java.time.LocalDateTime;

@JsonTypeName("FUENTEDEMO")
public class FuenteDemo extends FuenteProxy{
  @Getter
  private LocalDateTime fechaUltimaConsulta;
  @Getter
  private Conexion conexion;

  public FuenteDemo(String nombreFuente, URL endpointBase, Conexion conexion, HechoParser parser) {
    super(endpointBase, parser);
    this.nombre = nombreFuente;
    this.hechos = new ArrayList<>();
    this.fechaUltimaConsulta = LocalDateTime.now(ZoneId.of("UTC")).minusHours(1);
    this.conexion = new Conexion(){
      @Override
      public Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta) {
        return null;
      }
     };
    this.id = contadorID++;
    this.tipoFuente = TipoFuente.FUENTEDEMO;
  }

  public void actualizarHechos() {
    Map<String, Object> datos = conexion.siguienteHecho(this.getEndpointBase(), this.getFechaUltimaConsulta());
    while (datos != null) {
      Hecho nuevoHecho = new Hecho(
          (String) datos.get("titulo"),
          (String) datos.get("descripcion"),
          (String) datos.get("categoria"),
          (Float) datos.get("latitud"),
          (Float) datos.get("longitud"),
          (LocalDate) datos.get("fechaHecho"),
          (Perfil) datos.get("perfil"),
          (Boolean) datos.get("anonimo"),
          (List<Multimedia>) datos.get("multimedia")
          //Metamapa?
      );
      // Asignar perfil y anonimato según convenga
      nuevoHecho.setPerfil(null);
      nuevoHecho.setAnonimo(false);
      //verifica si ya existe
      boolean yaExiste = hechos.stream()
              .anyMatch(e -> e.getTitulo().equalsIgnoreCase(nuevoHecho.getTitulo()));
      // Agrego el hecho a la lista
      if (!yaExiste)
        hechos.add(nuevoHecho);
      // Actualizo fechaUltimaConsulta con la fecha del hecho si está disponible
      if (nuevoHecho.getFechaHecho() != null) {
        LocalDateTime fechaHecho = nuevoHecho.getFechaHecho().atStartOfDay();
        if (fechaHecho.isAfter(fechaUltimaConsulta)) {
          fechaUltimaConsulta = fechaHecho;
        }
      } else {
        fechaUltimaConsulta = LocalDateTime.now(ZoneId.of("UTC"));
      }
      // pido el siguiente hecho
      datos = conexion.siguienteHecho(this.getEndpointBase(), fechaUltimaConsulta);

    }
  }
}
/*Fuente Demo: una fuente que pueda dialogar con un sistema externo prototípico (y ficticio). En otras
palabras, se trata de una integración con un sistema externo ficticio, para el cual contamos con una
biblioteca cliente.  */