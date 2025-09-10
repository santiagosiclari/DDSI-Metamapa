package domain.business.incidencias;
import jakarta.persistence.*;
import domain.business.incidencias.Hecho;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "Multimedia")
public class Multimedia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "multimedia_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "multimedia_hecho_id", nullable = false)
  private Hecho hecho;

  @Column(name = "multimedia_data", length = 255, nullable = false)
  private String multimedia_data;

  public TipoMultimedia tipoMultimedia;
  String path;
  public Multimedia(){}

  public Multimedia(TipoMultimedia tipoMultimedia, String path) {
    this.tipoMultimedia = tipoMultimedia;
    this.path = path;
  }

}