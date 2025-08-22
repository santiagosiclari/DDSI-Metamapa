package FuenteEstatica.persistencia;

import FuenteEstatica.business.FuentesDeDatos.*;
import FuenteEstatica.business.Parsers.*;
import java.util.ArrayList;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  @Getter
  public ArrayList<FuenteEstatica> fuentesDeDatos = new ArrayList<>();
  @Getter
  public CSVHechoParser parserCSV = new CSVHechoParser();
  public void agregarFuente(FuenteEstatica fuente) {
    this.fuentesDeDatos.add(fuente);
  }

  public void RepositorioFuentes()
  {

  }

  public FuenteEstatica buscarFuente(Integer id) {
    return fuentesDeDatos.stream().filter(f-> f.getId() == id).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }
  /*
  public FuenteDeDatosDTO getFuenteDeDatosDTO(FuenteDeDatos fuente) {
    switch (fuente.getTipoFuente()) {
      case FUENTEDEMO:
        FuenteDemo fuenteDemo = (FuenteDemo) fuente;
        return FuenteDemoDTO.fromEntity(fuenteDemo);
      case FUENTEDINAMICA:

      case FUENTEESTATICA:

      case FUENTEMETAMAPA:

      case FUENTEPROXY:


      default:
        // Caso por defecto (opcional)
        throw new IllegalArgumentException(
            "Tipo de fuente desconocido: " + fuente.getTipoFuente()
        );
    }

  }
   */

  public RepositorioFuentes() {

    }
}


