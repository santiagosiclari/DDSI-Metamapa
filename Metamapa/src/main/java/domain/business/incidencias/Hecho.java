package domain.business.incidencias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import domain.business.FuentesDeDatos.FuenteDeDatos;
import domain.business.Usuarios.Perfil;
import domain.business.tiposSolicitudes.SolicitudEdicion;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;


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
  @Getter @Setter
  private Perfil perfil;
  @Getter @Setter
  private Boolean anonimo;
  @Getter @Setter
  private Boolean eliminado;
  @Getter @Setter
  private List<Multimedia> multimedia;
  @Getter @Setter
  private HashMap<String,String> metadata;
  @Getter
  private int id;

  //para pruebas con el repositorio
  static private int contadorID = 0;


  //TODO: Chequear si Categoria lo modelamos como string o un enum
  public Hecho(
      String titulo,
      String descripcion,
      String categoria,
      Float latitud,
      Float longitud,
      LocalDate fechaHecho,
      Perfil perfil,
      Boolean anonimo,
      List<Multimedia>multimedia) {

    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = new Ubicacion(latitud,longitud);
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDate.now();
    this.fechaModificacion = LocalDate.now();
    this.perfil = perfil;
    this.anonimo = anonimo;
    this.eliminado = false;
    this.multimedia = multimedia;
    //ArrayList<Pair<TipoMultimedia, String>> tuplaMultimedia
    //this.multimedia = tuplaMultimedia.stream().map(p -> new Multimedia(p.getValue0(),p.getValue1())).collect(Collectors.toCollection(ArrayList::new));
    this.metadata = new HashMap<>();
    this.id = contadorID++;

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


}