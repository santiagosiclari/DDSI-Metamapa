package domain.business.criterio;
import DTO.HechoDTO;
import domain.business.incidencias.Hecho;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "CriterioCategoria")
public class CriterioCategoria implements Criterio{
  @Id
  private Long id;

  @MapsId("coleccionId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "critCategoria_coleccion", nullable = false)
  private Coleccion coleccion;

  @Column(name = "critCategoria_categoria")
  private String critCategoria_categoria;

  @Column(name = "critCategoria_inclusion")
  private Boolean critCategoria_inclusion;

  private String categoria;

  public CriterioCategoria(String categoria) {
    this.categoria = categoria;
  }
  @Override
  public boolean cumple(Hecho hechoAValidar){
    String categoriaAValidar = hechoAValidar.getCategoria();

    return this.getCategoria().equalsIgnoreCase(categoriaAValidar);
  }
}
