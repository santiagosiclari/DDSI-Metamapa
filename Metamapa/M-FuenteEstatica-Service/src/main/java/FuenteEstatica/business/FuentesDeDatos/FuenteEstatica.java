package FuenteEstatica.business.FuentesDeDatos;
import FuenteEstatica.business.Parsers.*;
import FuenteEstatica.business.Hechos.Hecho;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.*;
import java.util.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Getter;
import lombok.*;

@JsonTypeName("FUENTEESTATICA")
@Getter @Setter
public class FuenteEstatica {
  static protected Integer contadorID = 20001;

  protected Integer fuenteId;
  public String nombre;
  public ArrayList<Hecho> hechos = new ArrayList<>();

  public FuenteEstatica() {}

  // Constructor para fuentes NUEVAS (Genera ID)
  public FuenteEstatica(String nombre) {
    this.nombre = nombre;
    this.fuenteId = contadorID++;
    this.hechos = new ArrayList<>();
  }

  // Constructor para cargar fuentes EXISTENTES desde disco
  public FuenteEstatica(Integer id, String nombre) {
    this.fuenteId = id;
    this.nombre = nombre;
    this.hechos = new ArrayList<>();
    if (id >= contadorID) {
      contadorID = id + 1;
    }
  }

  public void cargar(String tipo,String path) {
    if (tipo.equals("CSV")) {
      this.hechos.addAll(new CSVHechoParser().parsearHechos(path, this));
    } else {
      this.hechos.addAll(new CSVHechoParser().parsearHechos(path, this));
    }
  }

  public void cargarHechos(String path) {
    try (CSVReader reader = new CSVReader(new FileReader(path))) {
      String[] fila;
      while ((fila = reader.readNext()) != null) {
        Hecho hecho = new CSVHechoParser().parse(fila, this);
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
