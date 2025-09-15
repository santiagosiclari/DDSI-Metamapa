package java.FuenteDemo.persistencia;

import java.FuenteDemo.business.FuentesDeDatos.FuenteDemo;
import java.FuenteDemo.business.Parsers.CSVHechoParser;
import java.util.*;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  @Getter
  public ArrayList<FuenteDemo> fuentesDeDatos = new ArrayList<>();
  @Getter
  public CSVHechoParser parserCSV = new CSVHechoParser();
  public void agregarFuente(FuenteDemo fuente) {
    this.fuentesDeDatos.add(fuente);
  }

  public FuenteDemo buscarFuente(Integer id) {
    return fuentesDeDatos.stream().filter(f-> Objects.equals(f.getId(), id)).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }
  /*
  public FuenteDeDatosDTO getFuenteDeDatosDTO(FuenteDemo fuente) {
    switch (fuente.getTipoFuente()) {
      case FUENTEDEMO:
        FuenteDemo fuenteDemo = (FuenteDemo) fuente;
        return FuenteDemoDTO.fromEntity(fuenteDemo);

      default:
        // Caso por defecto (opcional)
        throw new IllegalArgumentException(
            "Tipo de fuente desconocido: " + fuente.getTipoFuente()
        );
    }

  }
   */
}