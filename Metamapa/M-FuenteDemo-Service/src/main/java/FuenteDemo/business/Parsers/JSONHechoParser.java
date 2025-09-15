package java.FuenteDemo.business.Parsers;

import java.FuenteDemo.business.Hechos.Hecho;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.util.ArrayList;

public abstract class JSONHechoParser implements HechoParser {
  @Override
 /* public List<Hecho> parsearHecho(String path){
    ArrayList<Hecho> listaHecho = new ArrayList<Hecho>();

    return listaHecho;
  }*/
  public ArrayList<Hecho> parsearHechos(String path, Integer fuenteID) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule()); // Para LocalDate
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignorar campos extra
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true); // Aceptar "" como null

    try {
      return mapper.readValue(new File(path), new TypeReference<ArrayList<Hecho>>() {});
    } catch (Exception e) {
      System.out.println("Error al leer el archivo JSON: " + e.getMessage());
      return new ArrayList<>(); // Devuelve lista vacía en caso de error
    }
  }
}