package domain.business.criterio;

import domain.business.incidencias.Hecho;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "CriterioFechaHecho")
public class CriterioFechaHecho implements Criterio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "critFechaHecho_coleccion")
  private Long id;

  @Column(name = "critFechaHecho_desde")
  private LocalDate critFechaHecho_desde;

  @Column(name = "critFechaHecho_hasta")
  private LocalDate critFechaHecho_hasta;

  @Column(name = "critFechaHecho_inclusion")
  private Boolean critFechaHecho_inclusion;

  public LocalDate fechaDesde;
  @Getter
  public LocalDate fechaHasta;

  public CriterioFechaHecho(LocalDate fechaDesde, LocalDate fechaHasta) {
    this.fechaDesde = fechaDesde;
    this.fechaHasta = fechaHasta;
  }

  public boolean cumple(Hecho hechoAValidar) {

    LocalDate fechaAValidar = hechoAValidar.getFechaHecho();
    if (this.getFechaHasta() == null) return !fechaAValidar.isBefore(this.getFechaDesde());
    if (this.getFechaDesde() == null) return !fechaAValidar.isAfter(this.getFechaHasta());
    return !fechaAValidar.isBefore(this.getFechaDesde()) && !fechaAValidar.isAfter(this.getFechaHasta());

  }
}
