package Agregador.business.Hechos;
import Agregador.business.Usuarios.Usuario;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;
import Agregador.business.Consenso.*;

@Entity
@Getter @Setter
public class Hecho {
  @Id
  private BigInteger id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDateTime fechaHecho;
  private LocalDateTime fechaCarga;
  private LocalDateTime fechaModificacion;
  @ManyToOne
  private Usuario perfil;
  private Boolean anonimo;
  private Boolean eliminado;
  private ArrayList<Multimedia> multimedia;
  @Getter
  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(
      name = "ConsensoHecho",
      joinColumns = @JoinColumn(name = "consensoHecho_hecho"),
      inverseJoinColumns = @JoinColumn(name = "consensoHecho_consenso")
  )
  private Set<Consenso> consensos;
  @ElementCollection
  @CollectionTable(name = "hecho_metadata", joinColumns = @JoinColumn(name = "hecho_id"))
  @MapKeyColumn(name = "clave")
  @Column(name = "valor")
  private Map<String, String> metadata;

  public Hecho(){}
  public Hecho(
          String titulo,
          String descripcion,
          String categoria,
          Float latitud,
          Float longitud,
          LocalDateTime fechaHecho,
          Usuario perfil,
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
    this.fechaCarga = LocalDateTime.now();
    this.fechaModificacion = LocalDateTime.now();
    this.perfil = perfil;
    this.anonimo = anonimo;
    this.eliminado = false;
    this.multimedia = multimedia;
    this.metadata = new HashMap<>();
    this.consensos = new HashSet<>();
    this.id = BigInteger.valueOf(fuenteId.longValue()).multiply(BASE).add(BigInteger.valueOf(hechoId.longValue()));
    //TODO FuenteId tiene que venir de la siguiente froma xyyyyyy siendo x el tipo de fuente 1 para dinamica, 2 para estaica, 3 para proxy. y despues yyyyyy es el id de la fuente. esto se logra para sumandole 1000000 a un id de fuente dinamica, 2000000 para estatica y 3000000 para proxu
    // prefijos 1000000/2000000/3000000 para tipo de fuente → ya quedan dentro de fuenteId

  }
  private static final BigInteger BASE = BigInteger.TEN.pow(12); // 10^12

  public Integer getIdFuente() {
    return this.id.divide(BASE).intValueExact();
  }

  public Boolean tieneEtiqueta(String key, String value) {
    return getMetadata().get(key).equals(value);
  }

  public void agregarConsenso(Consenso consenso) {
    consensos.add(consenso);
  }

  public boolean estaConsensuado(Consenso consenso) {
    //todo hacerlo por id
    return consensos.stream().anyMatch(c -> c.getClass() == consenso.getClass());
  }

  public void editarHecho(String titulo, String descripcion, String categoria, Float latitud, Float longitud, LocalDateTime fechaHecho, Boolean anonimidad, ArrayList<Multimedia> multimedia) {
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
    this.fechaModificacion = LocalDateTime.now();
  }

  public void aniadirEtiqueta(String key, String value) {
    if (this.tieneEtiqueta(key, value)) {
      throw new RuntimeException("Esa etiqueta ya existe");
    } else this.metadata.put(key, value);
  }
}