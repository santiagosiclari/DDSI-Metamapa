package Metamapa.business.Hechos;

import Metamapa.business.Usuarios.Usuario;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
  private Usuario usuario;
  @Getter
  private BigInteger id;
  @Getter @Setter
  private Boolean anonimo;
  @Getter @Setter
  private Boolean eliminado;
  @Getter @Setter
  private ArrayList<Multimedia> multimedia;
  @Getter @Setter
  private HashMap<String, String> metadata;


  //TODO: Chequear si Categoria lo modelamos como string o un enum
  public Hecho(){}
  public Hecho(
      String titulo,
      String descripcion,
      String categoria,
      Float latitud,
      Float longitud,
      LocalDate fechaHecho,
      Usuario usuario,
      Integer fuenteId,
      Integer hechoId,
      Boolean anonimo,
      ArrayList<Multimedia> multimedia) {

    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDate.now();
    this.fechaModificacion = LocalDate.now();
    this.usuario = usuario;
    this.anonimo = anonimo;
    this.eliminado = false;
    this.multimedia = multimedia;
    this.metadata = new HashMap<>();
    this.id = BigInteger.valueOf(fuenteId).multiply(BigInteger.TEN.pow(12)).add(BigInteger.valueOf(hechoId)); //TODO FuenteId tiene que venir de la siguiente froma xyyyyyy siendo x el tipo de fuente 1 para dinamica, 2 para estaica, 3 para proxy. y despues yyyyyy es el id de la fuente. esto se logra para sumandole 1000000 a un id de fuente dinamica, 2000000 para estatica y 3000000 para proxu

  }

  public Boolean tieneEtiqueta(String key, String value) {
    return getMetadata().get(key).equals(value);
  }

  public void editarHecho(String titulo, String descripcion, String categoria, Float latitud, Float longitud, LocalDate fechaHecho, Boolean anonimidad, ArrayList<Multimedia> multimedia) {
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
    if (anonimidad != null) {
      this.anonimo = anonimidad;
    }
    if (multimedia != null) {
      this.multimedia = multimedia;
    }
    this.fechaModificacion = LocalDate.now();
  }

  public void aniadirEtiqueta(String key, String value) {
    if (this.tieneEtiqueta(key, value)) {
      throw new RuntimeException("Esa etiqueta ya existe");
    } else this.metadata.put(key, value);
  }
}