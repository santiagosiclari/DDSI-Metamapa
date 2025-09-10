package domain.business.incidencias;

import domain.business.Usuarios.Perfil;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter @Setter
@AllArgsConstructor
@Entity
@Table(name = "hecho")
public class Hecho {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "hecho_ID")
  private Long id;

  @Column(name = "hecho_titulo", length = 255, nullable = false)
  private String titulo;

  @Column(name = "hecho_descripcion", length = 255)
  private String descripcion;

  @Column(name = "hecho_categoria", length = 255)
  private String categoria;

  @Column(name = "hecho_fecha_hecho")
  private LocalDate fechaHecho;

  @Column(name = "hecho_fecha_mod")
  private LocalDate fechaModificacion;

  // FK a la fuente (cuando exista la entidad Fuente, podés migrar a @ManyToOne)
  @Column(name = "hecho_fuenteID")
  private Long fuenteId;

  @Column(name = "hecho_anonimo")
  private Boolean anonimo;

  @Column(name = "hecho_eliminado")
  private Boolean eliminado = false;

  // Ubicación embebida → columnas flatten del DER
  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "latitud",  column = @Column(name = "hecho_latitud",  precision = 12, scale = 2)),
          @AttributeOverride(name = "longitud", column = @Column(name = "hecho_longitud", precision = 12, scale = 2))
  })
  private Ubicacion ubicacion;

  // Multimedia del Hecho (tabla separada en el DER)
  @OneToMany(mappedBy = "hecho")
  private List<Multimedia> multimedia;

  // Si conservás metadatos en memoria y no están en el DER:
  @Transient
  private Map<String,String> metadata = new HashMap<>();

//TODO: Chequear si Categoria lo modelamos como string o un enum
  public Hecho(){}
  public Hecho(
      String titulo,
      String descripcion,
      String categoria,
      Float latitud,
      Float longitud,
      LocalDate fechaHecho,
      Long fuenteId,
      Boolean anonimo,
      List<Multimedia> multimedia) {

    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = new Ubicacion(latitud,longitud);
    this.fechaHecho = fechaHecho;
    this.fechaModificacion = LocalDate.now();
    this.fuenteId = fuenteId; //AGREGO ESTE CAMPO
    this.anonimo = anonimo;
    this.eliminado = false;
    this.multimedia = multimedia;
    //ArrayList<Pair<TipoMultimedia, String>> tuplaMultimedia
    //this.multimedia = tuplaMultimedia.stream().map(p -> new Multimedia(p.getValue0(),p.getValue1())).collect(Collectors.toCollection(ArrayList::new));
    this.metadata = new HashMap<>();
  }

  public Boolean tieneEtiqueta(String key,String value) {
    return getMetadata().get(key).equals(value);
  }

  public void editarHecho(String titulo, String descripcion, String categoria, Float latitud, Float longitud, LocalDate fechaHecho, Boolean anonimidad, Set<Multimedia> multimedia) {
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
      Ubicacion ubicacionMod = new Ubicacion(latitud,longitud);
      this.ubicacion = ubicacionMod;
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
    if (this.tieneEtiqueta(key,value)) {
      throw new RuntimeException("Esa etiqueta ya existe");
    }else this.metadata.put(key,value);
  }


}