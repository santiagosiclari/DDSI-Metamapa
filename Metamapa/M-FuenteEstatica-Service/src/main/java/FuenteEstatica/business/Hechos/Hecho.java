package FuenteEstatica.business.Hechos;
import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter
public class Hecho {
  private Integer id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDateTime fechaHecho;
  private LocalDateTime fechaCarga;
  private Integer fuenteId;
  static public Integer contadorID = 1;

  public Hecho(
          String titulo,
          String descripcion,
          String categoria,
          Float latitud,
          Float longitud,
          LocalDateTime fechaHecho,
          Integer fuenteId) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDateTime.now();
    this.id = contadorID++;
    this.fuenteId = fuenteId;
  }

  public Hecho() {}
}