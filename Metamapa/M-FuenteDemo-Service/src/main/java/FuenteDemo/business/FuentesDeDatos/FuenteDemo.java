package FuenteDemo.business.FuentesDeDatos;
import FuenteDemo.business.Conexiones.Conexion;
import FuenteDemo.business.Hechos.Hecho;
import com.fasterxml.jackson.annotation.*;
import java.time.*;
import java.util.*;
import lombok.*;
import jakarta.persistence.*;

@JsonTypeName("FUENTEDEMO")
@Getter @Entity
public class FuenteDemo {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuentesContador")
  @SequenceGenerator(name = "fuentesContador", sequenceName = "fuentesContador", initialValue = 5000000, allocationSize = 1)
  protected Integer id;
  public String nombre;
  @OneToMany
  public ArrayList<Hecho> hechos;
  private LocalDateTime fechaUltimaConsulta;
  @JsonIgnore @Transient
  final private Conexion conexion;

  public String endpointBase;

  public FuenteDemo() {
    this.conexion = new Conexion();
  }
  public FuenteDemo(String nombreFuente, String endpointBase) {
      this.nombre = nombreFuente;
      this.hechos = new ArrayList<>();
      this.endpointBase = endpointBase;
      this.fechaUltimaConsulta = LocalDateTime.now(ZoneId.of("UTC")).minusHours(1);
      this.conexion = new Conexion();
  }

  public void actualizarHechos() {
    Map<String, Object> datos = conexion.siguienteHecho(this.endpointBase,this.getFechaUltimaConsulta());
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
      datos = conexion.siguienteHecho(this.endpointBase,fechaUltimaConsulta);
    }
  }
}
/*Fuente Demo: una fuente que pueda dialogar con un sistema externo prototípico (y ficticio). En otras
palabras, se trata de una integración con un sistema externo ficticio, para el cual contamos con una
biblioteca cliente.  */