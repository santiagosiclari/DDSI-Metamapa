package FuenteEstatica.business.FuentesDeDatos;
import FuenteEstatica.business.Parsers.*;
import FuenteEstatica.business.Hechos.Hecho;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.*;
import java.util.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Getter;

@JsonTypeName("FUENTEESTATICA")

public class FuenteEstatica {
  @Getter
  static protected Integer contadorID = 2000000;
  @Getter
  protected Integer id;
  @Getter
  public String nombre;
  @Getter
  public ArrayList<Hecho> hechos;
  /*@Getter
  public HechoParser hechoParser;*/

  public FuenteEstatica() {} // va a haber que usar dtos para no modificar la capa de negocio
  public FuenteEstatica(String nombre) {
    if (contadorID > 2999998) {
      throw new RuntimeException("No hay mas espacio para nuevas fuentes :(");
    } else {
      this.nombre = nombre;
      this.id = contadorID++;
      this.hechos = new ArrayList<>();
    }
  }

  public void cargar(String tipo,String path) {
    switch (tipo) {
      case "CSV": new CSVHechoParser().parsearHechos(path, id).forEach(hecho -> this.hechos.add(hecho));
      break;
      //case "JSON": new JSONHechoParser().parsearHechos(path, id).forEach((this::agregarHecho));  arreglar el codigo para que tome un JSON?
      //break;
      default: new CSVHechoParser().parsearHechos(path, id).forEach(hecho -> this.hechos.add(hecho));
      break;
    }
  }

  public void cargarHechos(String path) {
    try (CSVReader reader = new CSVReader(new FileReader(path))) {
      String[] fila;
      while ((fila = reader.readNext()) != null) {
        Hecho hecho = new CSVHechoParser().parse(fila, id);
        // buscar si ya existe un hecho con el mismo título
        Optional<Hecho> existente = hechos.stream()
                .filter(h -> h.getTitulo().equalsIgnoreCase(hecho.getTitulo()))
                .findFirst();
        if (existente.isPresent()) {
          // pisar los atributos del existente
          Hecho h = existente.get();
          h.setDescripcion(hecho.getDescripcion());
          h.setCategoria(hecho.getCategoria());
          h.setLatitud(hecho.getLatitud());
          h.setLongitud(hecho.getLongitud());
          h.setFechaHecho(hecho.getFechaHecho());
        } else {
          hechos.add(hecho);
        }
      }
    } catch (IOException | CsvValidationException e) {
      throw new RuntimeException("Error al cargar hechos desde CSV", e);
    }
  }
/*
  public void cargarJSON(String path) {
    new JSONHechoParser().parsearHechos(path, id).forEach((this::agregarHecho));
  }
*/
}
