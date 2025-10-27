package FuenteDemo.business.Hechos;
import FuenteDemo.business.FuentesDeDatos.FuenteDemo;
import java.time.LocalDate;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter @Setter
public class Hecho {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDate fechaHecho;
  private LocalDate fechaModificacion;
  @Setter
  @ManyToOne
  private FuenteDemo fuente;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer id;


  public Hecho() {}
  public Hecho(
          String titulo,
          String descripcion,
          String categoria,
          Float latitud,
          Float longitud,
          LocalDate fechaHecho,
          FuenteDemo fuente) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaHecho = fechaHecho;
    this.fechaModificacion = LocalDate.now();
    this.fuente = fuente; //AGREGO ESTE CAMPO
  }

  public void editarHecho(String titulo, String descripcion, String categoria, Float latitud, Float longitud, LocalDate fechaHecho) {
    if (titulo != null) {
      this.titulo = titulo;
    }
    if (descripcion != null) {
      this.descripcion = descripcion;
    }
    if (categoria != null) {
      this.categoria = categoria;
    }
    if (latitud != null & longitud != null) {
      this.latitud = latitud;
      this.longitud = longitud;
    }
    if (fechaHecho != null) {
      this.fechaHecho = fechaHecho;
    }
    this.fechaModificacion = LocalDate.now();
  }

}