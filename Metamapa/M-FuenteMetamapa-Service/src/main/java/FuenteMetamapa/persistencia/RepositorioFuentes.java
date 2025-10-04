package FuenteMetamapa.persistencia;
import FuenteMetamapa.business.FuentesDeDatos.*;
import FuenteMetamapa.business.Parsers.CSVHechoParser;
import java.util.*;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  @Getter
  public ArrayList<FuenteMetamapa> fuentesDeDatos = new ArrayList<>();
  @Getter
  public CSVHechoParser parserCSV = new CSVHechoParser();
  public void agregarFuente(FuenteMetamapa fuente) {
    this.fuentesDeDatos.add(fuente);
  }

  public FuenteMetamapa buscarFuente(Integer id) {
    return fuentesDeDatos.stream().filter(f-> Objects.equals(f.getId(), id)).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }
  /*
  public FuenteDeDatosDTO getFuenteDeDatosDTO(FuenteDeDatos fuente) {
    switch (fuente.getTipoFuente()) {
      case FUENTEDEMO:
        FuenteDemo fuenteDemo = (FuenteDemo) fuente;
        return FuenteDemoDTO.fromEntity(fuenteDemo);

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