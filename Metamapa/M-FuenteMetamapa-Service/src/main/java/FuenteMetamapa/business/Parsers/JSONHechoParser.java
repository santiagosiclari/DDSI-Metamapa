package FuenteMetamapa.business.Parsers;
import FuenteMetamapa.business.FuentesDeDatos.FuenteMetamapa;
import FuenteMetamapa.business.Hechos.Hecho;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class JSONHechoParser implements HechoParser {
 /* public List<Hecho> parsearHecho(String path){
    ArrayList<Hecho> listaHecho = new ArrayList<Hecho>();

    return listaHecho;
  }*/
  @Override
  public ArrayList<Hecho> parsearHechos(String path, FuenteMetamapa fuenteID) {
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

  public ArrayList<Hecho> parsearHechos(InputStream in, FuenteMetamapa fuenteID)
  {
    return null;
  }
}