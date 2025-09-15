package java.FuenteMetamapa.business.Hechos;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Hecho {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDate fechaHecho;
  private LocalDate fechaCarga;
  private LocalDate fechaModificacion;
  //@Getter @Setter
  //private Perfil perfil;
  @Setter
  private int fuenteId;
  //@Getter @Setter
  //private Boolean anonimo;
  //@Getter @Setter
  //private Boolean eliminado;
  //@Getter @Setter
  //private List<Multimedia> multimedia;
  //@Getter @Setter
  //private HashMap<String,String> metadata;
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
          Integer fuenteId) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDate.now();
    this.fechaModificacion = LocalDate.now();
    this.fuenteId = fuenteId; //AGREGO ESTE CAMPO
    //ArrayList<Pair<TipoMultimedia, String>> tuplaMultimedia
    //this.multimedia = tuplaMultimedia.stream().map(p -> new Multimedia(p.getValue0(),p.getValue1())).collect(Collectors.toCollection(ArrayList::new));
    //this.metadata = new HashMap<>();
    this.id = contadorID++;
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