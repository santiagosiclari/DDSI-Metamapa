package Agregador.business.Hechos;
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
  @Column(length = 1000)
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDateTime fechaHecho;
  private LocalDateTime fechaCarga;
  private LocalDateTime fechaModificacion;
  private Integer usuarioId;
  private Boolean anonimo;
  private Boolean eliminado;
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "hecho_id")
  private List<Multimedia> multimedia = new ArrayList<>();
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
  private String provincia;
  public Hecho() {}
  //private GeocodingService geocodingService = new GeocodingService();
  public Hecho(
          String titulo,
          String descripcion,
          String categoria,
          Float latitud,
          Float longitud,
          LocalDateTime fechaHecho,
          Integer usuarioId,
          Integer fuenteId,
          String provincia,
          Integer hechoId,
          Boolean anonimo,
          ArrayList<Multimedia> multimedia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.provincia = provincia;
    this.fechaHecho = fechaHecho;
    this.fechaCarga = LocalDateTime.now();
    this.fechaModificacion = LocalDateTime.now();
    this.usuarioId = usuarioId;
    this.anonimo = anonimo;
    this.eliminado = false;
    this.multimedia = multimedia;
    this.metadata = new HashMap<>();
    this.consensos = new HashSet<>();
    this.id = BigInteger.valueOf(fuenteId.longValue()).multiply(BASE).add(BigInteger.valueOf(hechoId.longValue()));
  }

  private static final BigInteger BASE = BigInteger.TEN.pow(9);

  public Integer getIdFuente() {
    return this.id.divide(BASE).intValueExact();
  }

  public Boolean tieneEtiqueta(String key, String value) {
    return value != null && value.equals(metadata.get(key));
  }

  public void agregarConsenso(Consenso consenso) {
    consensos.add(consenso);
  }

  public boolean estaConsensuado(Consenso consenso) {
    return consensos.stream().anyMatch(c -> c.getId().equals(consenso.getId()));
  }

  public void editarHecho(String titulo, String descripcion, String categoria, Float latitud, Float longitud, LocalDateTime fechaHecho, Boolean anonimidad) {
    if (titulo != null)
      this.titulo = titulo;
    if (descripcion != null)
      this.descripcion = descripcion;
    if (categoria != null)
      this.categoria = categoria;
    if (latitud != null & longitud != null) {
      this.latitud = latitud;
      this.longitud = longitud;
    }
    if (fechaHecho != null)
      this.fechaHecho = fechaHecho;
    if (anonimidad != null)
      this.anonimo = anonimidad;
    this.fechaModificacion = LocalDateTime.now();
  }

  public void aniadirEtiqueta(String key, String value) {
    if (this.tieneEtiqueta(key, value)) {
      throw new RuntimeException("Esa etiqueta ya existe");
    } else this.metadata.put(key, value);
  }
}