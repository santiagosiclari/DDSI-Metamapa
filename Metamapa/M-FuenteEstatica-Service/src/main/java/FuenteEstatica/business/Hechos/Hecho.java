package FuenteEstatica.business.Hechos;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

public class Hecho {
  @Getter
  private String titulo;
  @Getter
  private String descripcion;
  @Getter
  private String categoria;
  @Getter
  private Ubicacion ubicacion;
  @Getter
  private LocalDate fechaHecho;
  @Getter
  private LocalDate fechaCarga;
  @Getter
  private int fuenteId;
  @Getter @Setter
  private Integer id;
  static public Integer contadorID = 1;

  //TODO: Chequear si Categoria lo modelamos como string o un enum
  public Hecho(){}
  public Hecho(
      String titulo,
      String descripcion,
      String categoria,
      Float latitud,
      Float longitud,
      LocalDate fechaHecho,
      int fuenteId) {

    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = new Ubicacion(latitud,longitud);
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDate.now();
    this.id = contadorID++;
    this.fuenteId = fuenteId;
  }

  public void editarHecho(String descripcion, String categoria, Float latitud, Float longitud, LocalDate fechaHecho) {
    if (descripcion != null) {
      this.descripcion = descripcion;
    }
    if (categoria != null) {
      this.categoria = categoria;
    }
    if (latitud != null & longitud != null) {
      this.ubicacion = new Ubicacion(latitud,longitud);
    }
    if (fechaHecho != null) {
      this.fechaHecho = fechaHecho;
    }
  }
}