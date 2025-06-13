package domain.business.criterio;
import domain.business.incidencias.Hecho;
import java.time.LocalDate;
import lombok.Getter;

public class CriterioFecha implements Criterio{
      @Getter
      public LocalDate fechaDesde;
      @Getter
      public LocalDate fechaHasta;

    public CriterioFecha(LocalDate fechaDesde, LocalDate fechaHasta) {
    this.fechaDesde = fechaDesde;
    this.fechaHasta = fechaHasta;
  }
  public boolean cumple(Hecho hechoAValidar){

    LocalDate fechaAValidar = hechoAValidar.getFechaHecho();

    return !fechaAValidar.isBefore(this.getFechaDesde()) && !fechaAValidar.isAfter(this.getFechaHasta());

    }
}
