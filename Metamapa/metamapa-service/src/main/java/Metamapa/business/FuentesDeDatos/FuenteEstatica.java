package Metamapa.business.FuentesDeDatos;
import Metamapa.business.Parsers.CSVHechoParser;
import Metamapa.business.Hechos.Hecho;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
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
      //case "JSON": new JSONHechoParser().parsearHechos(path, id).forEach((this::agregarHecho)); TODO arreglar el codigo para que tome un JSON?
      //break;
      default: new CSVHechoParser().parsearHechos(path, id).forEach(hecho -> this.hechos.add(hecho));
        break;
    }
  }
/*
  public void cargarJSON(String path) {
    new JSONHechoParser().parsearHechos(path, id).forEach((this::agregarHecho));
  }
*/
}