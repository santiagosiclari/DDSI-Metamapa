package domain.business.criterio;
import domain.business.incidencias.Hecho;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "CriterioTitulo")
public class CriterioTitulo implements Criterio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "critTitulo_coleccion")
  private Long critTitulo_coleccion;

  @Column(name = "critTitulo_titulo")
  private String critTitulo_titulo;
  @Column(name = "critTitulo_inclusion")
  private Boolean critTitulo_inclusion;

  private String titulo;

  public CriterioTitulo(String titulo) {
    this.titulo = titulo;
  }

  @Override
  public boolean cumple(Hecho hechoAValidar){
    String tituloAValidar = hechoAValidar.getTitulo();
    return this.getTitulo().equals(tituloAValidar);
  }
}