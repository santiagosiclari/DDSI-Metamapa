package FuenteDinamica.business.Hechos;
import java.time.LocalDate;
import java.util.*;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "hecho")
@Getter @Setter
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDate fechaHecho;
  private LocalDate fechaCarga;
  private LocalDate fechaModificacion;
  private Integer idUsuario;
  private Boolean anonimo;
  private Boolean eliminado = false;
  @ManyToOne
  @JoinColumn(name = "fuente_id")
  private FuenteDinamica fuente;
  @OneToMany(mappedBy = "hecho", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Multimedia> multimedia = new ArrayList<>();
  @ElementCollection
  @CollectionTable(name = "hecho_metadata", joinColumns = @JoinColumn(name = "hecho_id"))
  @MapKeyColumn(name = "clave")
  @Column(name = "valor")
  private Map<String, String> metadata = new HashMap<>();

  public Hecho() {}

  public Hecho(
          String titulo,
          String descripcion,
          String categoria,
          Float latitud,
          Float longitud,
          LocalDate fechaHecho,
          Integer idUsuario,
          FuenteDinamica fuente, // <--- aquí pasás la referencia, no el ID
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
    this.fuente = fuente; // <--- Hibernate maneja la FK
    this.anonimo = anonimo;
    this.eliminado = false;
    this.multimedia = multimedia;
  }
}