package domain.business.criterio;
import domain.business.incidencias.Hecho;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter@Setter
@Entity
@Table(name = "CriterioFechaReportaje")
public class CriterioFechaReportaje implements Criterio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "critFechaReportaje_coleccion")
  private Long critFechaReportaje_coleccion;

  @Column(name = "critFechaReportaje_desde")
  private LocalDate critFechaReportaje_desde;

  @Column(name = "critFechaReportaje_hasta")
  private LocalDate critFechaReportaje_hasta;

  @Column(name = "critFechaReportaje_inclusion")
  private Boolean critFechaReportaje_inclusion;

  private final LocalDate desde;
  private final LocalDate hasta;

  public CriterioFechaReportaje(LocalDate desde, LocalDate hasta) {
    this.desde = desde;
    this.hasta = hasta;
  }

  public boolean cumple(Hecho hecho) {
    LocalDate fecha = hecho.getFechaCarga();
    return (desde == null || !fecha.isBefore(this.getDesde())) &&
        (hasta == null || !fecha.isAfter(this.getHasta()));
  }
}