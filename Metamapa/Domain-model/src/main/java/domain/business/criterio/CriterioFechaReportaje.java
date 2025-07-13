package domain.business.criterio;
import domain.business.incidencias.Hecho;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class CriterioFechaReportaje implements Criterio {
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