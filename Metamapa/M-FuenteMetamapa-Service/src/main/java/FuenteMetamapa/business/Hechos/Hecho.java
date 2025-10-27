package FuenteMetamapa.business.Hechos;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import FuenteMetamapa.business.FuentesDeDatos.FuenteMetamapa;

@Getter @Setter
@Entity
public class Hecho {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDate fechaHecho;
  private LocalDate fechaCarga;
  private LocalDate fechaModificacion;

  @ManyToOne
  private FuenteMetamapa fuente;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer id;
  static public Integer contadorID = 1;

  public Hecho(){}
  public Hecho(
          String titulo,
          String descripcion,
          String categoria,
          Float latitud,
          Float longitud,
          LocalDate fechaHecho,
          FuenteMetamapa fuenteId) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDate.now();
    this.fechaModificacion = LocalDate.now();
    this.fuente = fuenteId;
  }

  /*public Boolean tieneEtiqueta(String key,String value) {
    return getMetadata().get(key).equals(value);
  }*/

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

  /*public void aniadirEtiqueta(String key, String value) {
    if (this.tieneEtiqueta(key,value)) {
      throw new RuntimeException("Esa etiqueta ya existe");
    }else this.metadata.put(key,value);
  }*/
}