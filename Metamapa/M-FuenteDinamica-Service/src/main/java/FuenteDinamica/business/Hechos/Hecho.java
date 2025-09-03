package FuenteDinamica.business.Hechos;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

@Getter @Setter
public class Hecho {
  private Integer id; // <-- necesario para setId(...)
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDate fechaHecho;
  private LocalDate fechaCarga;
  private LocalDate fechaModificacion;
  private Integer idUsuario;
  private Integer fuenteId;
  private Boolean anonimo;
  private Boolean eliminado;
  private List<Multimedia> multimedia;
  private HashMap<String,String> metadata;

  public Hecho() {}

  public Hecho(
      String titulo,
      String descripcion,
      String categoria,
      Float latitud,
      Float longitud,
      LocalDate fechaHecho,
      Integer idUsuario,
      Integer fuenteId,
      Boolean anonimo,
      List<Multimedia> multimedia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDate.now();
    this.fechaModificacion = LocalDate.now();
    this.idUsuario = idUsuario;
    this.fuenteId = fuenteId; //AGREGO ESTE CAMPO
    this.anonimo = anonimo;
    this.eliminado = false;
    this.multimedia = multimedia;
  }
}