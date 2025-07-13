package domain.business.criterio;
import DTO.HechoDTO;
import domain.business.incidencias.Hecho;
import lombok.Getter;

public class CriterioCategoria implements Criterio{
  @Getter
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
