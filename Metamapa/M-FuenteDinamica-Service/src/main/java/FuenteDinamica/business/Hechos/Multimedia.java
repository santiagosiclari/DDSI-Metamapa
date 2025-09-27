package FuenteDinamica.business.Hechos;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "multimedia")
@Getter @Setter
public class Multimedia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Enumerated(EnumType.STRING)
  private TipoMultimedia tipoMultimedia;
  private String path;
  @ManyToOne
  @JoinColumn(name = "hecho_id")
  private Hecho hecho;

  public Multimedia(){}

  public Multimedia(TipoMultimedia tipoMultimedia, String path) {
    this.tipoMultimedia = tipoMultimedia;
    this.path = path;
  }
}