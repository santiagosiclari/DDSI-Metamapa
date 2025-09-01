package Metamapa.business.FuentesDeDatos;

import Metamapa.business.Conexiones.Conexion;
import Metamapa.business.Hechos.Hecho;
import Metamapa.business.Usuarios.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Map;


@JsonTypeName("FUENTEDEMO")
public class FuenteDemo extends FuenteProxy {
  @Getter
  private LocalDateTime fechaUltimaConsulta;
  @Getter
  @JsonIgnore
  final private Conexion conexion;
  static private Integer contadorID = 5000000;

  public FuenteDemo(String nombreFuente, String endpointBase) {
    super(nombreFuente, endpointBase);
    if (contadorID > 5999999) {
      throw new RuntimeException("No hay mas espacio para nuevas Fuentes Demo :(");
    } else {
      this.nombre = nombreFuente;
      this.hechos = new ArrayList<>();
      this.fechaUltimaConsulta = LocalDateTime.now(ZoneId.of("UTC")).minusHours(1);
      this.conexion = new Conexion() {
        @Override
        public Map<String, Object> siguienteHecho(String url, LocalDateTime fechaUltimaConsulta) {
          return null;
        }
      };
      this.id = contadorID++;
      this.tipoFuente = TipoFuente.FUENTEDEMO;
    }
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
          new Usuario("san", "sa", "sa", "sa", 16, null),
          this.id,
          1,
          null,null
          //Metamapa?
      );
      // Asignar perfil y anonimato según convenga
      //nuevoHecho.setPerfil(null);
      //nuevoHecho.setAnonimo(false);
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