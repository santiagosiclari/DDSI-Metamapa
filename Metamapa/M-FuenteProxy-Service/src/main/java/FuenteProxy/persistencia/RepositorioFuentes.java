package FuenteProxy.persistencia;
import FuenteProxy.business.FuentesDeDatos.*;
import FuenteProxy.business.Parsers.CSVHechoParser;
import java.time.LocalDate;
import java.util.*;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  @Getter
  public ArrayList<FuenteProxy> fuentesDeDatos = new ArrayList<>();
  @Getter
  public CSVHechoParser parserCSV = new CSVHechoParser();
  public void agregarFuente(FuenteProxy fuente) {
    this.fuentesDeDatos.add(fuente);
  }

  public FuenteProxy buscarFuente(Integer id) {
    return fuentesDeDatos.stream().filter(f-> Objects.equals(f.getId(), id)).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
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
}


