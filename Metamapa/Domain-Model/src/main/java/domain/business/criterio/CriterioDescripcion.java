package domain.business.criterio;

import domain.business.incidencias.Hecho;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Criterio_Descripcion")
public class CriterioDescripcion implements Criterio{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "critDescripcion_coleccion")
  private Long critDescripcion_coleccion;

  @Column(name = "critDescripcion_descripcion")
  private String critDescripcion_descripcion;
  @Column(name = "critDescripcion_inclusion")
  private Boolean critDescripcion_inclusion;

  private String descripcion;

  public CriterioDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
  @Override
  public boolean cumple(Hecho hechoAValidar){
    String descripcionAValidar = hechoAValidar.getDescripcion();

    return this.descripcion.equals(descripcionAValidar);
  }
}
