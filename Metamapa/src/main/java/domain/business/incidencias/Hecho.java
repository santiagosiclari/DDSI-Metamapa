package domain.business.incidencias;


import com.fasterxml.jackson.annotation.JsonIgnore;
import domain.business.FuentesDeDatos.FuenteDeDatos;
import domain.business.Usuarios.Perfil;
import domain.business.tiposSolicitudes.SolicitudEdicion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
  private final LocalDate fechaCarga;
  @Getter
  private LocalDate fechaModificacion;
  @Getter
  private FuenteDeDatos fuenteDeDatos;
  @Getter
  private Perfil autor;
  @Getter
  private Boolean anonimo;
  @Getter @Setter
  private Boolean eliminado;
  @Getter
  private ArrayList<Multimedia> multimedia;
  @Getter
  private HashMap<String,String> metadata;


  //TODO: revisar constructor modificado con menos atributos??

  public Hecho(String titulo, String desc, String categoria, Float latitud, Float longitud, LocalDate fechaHecho) {
    this.titulo = titulo;
    this.descripcion = desc;
    this.categoria = categoria;
    this.ubicacion = new Ubicacion(latitud,longitud);
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDate.now();
    this.fechaModificacion = LocalDate.now();
    this.fuenteDeDatos = null;
    this.autor = null;
    this.anonimo = false;
    this.eliminado = false;
    this.multimedia = new ArrayList<>();
    this.metadata = new HashMap<>();
  }

  public Boolean tieneEtiqueta(String key,String value) {
    return getMetadata().get(key).equals(value);
  }

  public void editarHecho(SolicitudEdicion solicitud) {
    if (solicitud.getTituloMod() != null) {
      this.titulo = solicitud.getTituloMod();
    }
    if (solicitud.getDescMod() != null) {
      this.descripcion = solicitud.getDescMod();
    }
    if (solicitud.getCategoriaMod() != null) {
      this.categoria = solicitud.getCategoriaMod();
    }
    if (solicitud.getUbicacionMod() != null) {
      this.ubicacion = solicitud.getUbicacionMod();
    }
    if (solicitud.getAnonimidadMod() != null) {
      this.anonimo = solicitud.getAnonimidadMod();
    }
    this.fechaModificacion = LocalDate.now();
  }

  public void aniadirEtiqueta(String key, String value) {
    if (this.tieneEtiqueta(key,value)) {
      throw new RuntimeException("Esa etiqueta ya existe");
    }else this.metadata.put(key,value);
  }
  // TODO: agregar al diagrama de clases, rta Que es actualizarse?
//  public void actualizarse(String )
//  {
//
//  }

  @JsonIgnore
  public String getNombreAutor() {
    if (this.getFuenteDeDatos() == null) {
      return "Fuente desconocida";
    }

    if (!this.getFuenteDeDatos().getClass().getSimpleName().equals("FuenteDinamica")) {
      return this.getFuenteDeDatos().getNombre();
    }

    return this.getAutor() != null
            ? this.getAutor().getNombre() + " " + this.getAutor().getApellido()
            : "Autor desconocido";
  }
}
