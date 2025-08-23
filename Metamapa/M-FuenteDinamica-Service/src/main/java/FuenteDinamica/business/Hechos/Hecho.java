package FuenteDinamica.business.Hechos;

import java.time.LocalDate;
import java.util.*;
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
  private Float latitud;
  @Getter
  private Float longitud;
  @Getter
  private LocalDate fechaHecho;
  @Getter
  private LocalDate fechaCarga;
  @Getter
  private LocalDate fechaModificacion;
  @Getter @Setter
  private Integer idUsuario;
  @Getter @Setter
  private int fuenteId;
  @Getter @Setter
  private Boolean anonimo;
  @Getter @Setter
  private Boolean eliminado;
  @Getter @Setter
  private List<Multimedia> multimedia;
  @Getter @Setter
  private HashMap<String,String> metadata;

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
    this.idUsuario = idUsuario;// idUsuario.getPerfil();
    this.fuenteId = fuenteId; //AGREGO ESTE CAMPO
    this.anonimo = anonimo;
    this.eliminado = false;
    this.multimedia = multimedia;
  }
}