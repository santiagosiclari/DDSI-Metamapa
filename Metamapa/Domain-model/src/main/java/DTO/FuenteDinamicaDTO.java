package DTO;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;

@JsonTypeName("FUENTEDINAMICA")
public class FuenteDinamicaDTO extends FuenteDeDatosDTO {

  public FuenteDinamicaDTO(Integer id) {
    this.id = id;
    this.nombre = "Fuente Dinamica";
    this.hechos =  new ArrayList<>();
    this.tipoFuente = tipoFuente.FUENTEDINAMICA;
  }
}