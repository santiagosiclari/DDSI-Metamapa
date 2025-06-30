package domain.business.FuentesDeDatos;


import com.fasterxml.jackson.annotation.JsonTypeName;
import domain.business.Parsers.HechoParser;
import domain.business.incidencias.Hecho;
import java.util.ArrayList;
import lombok.Getter;
@JsonTypeName("FUENTEESTATICA")

public class FuenteEstatica extends FuenteDeDatos{

  //@Getter
  //private String pathCSV;

  @Getter
  private HechoParser parser;
  public FuenteEstatica( String nombre, HechoParser parser){
    this.nombre = nombre;
    this.id = contadorID++;
   // this.pathCSV = pathCSV;
    this.parser = parser;
    this.hechos = new ArrayList<>();
    this.tipoFuente = tipoFuente.FUENTEESTATICA;
  }
//TODO: ver bien estas cosas. me quede sin energias, revise hasta Usuarios,tiposSolicitudes(cambiar nombre a solicitudes),Criterios ,incidencias y vi algo de fuentes.

  public void agregarHecho(){throw new UnsupportedOperationException("Not supported yet.");};

  public void cargarCSV(String pathCSV){
    ArrayList<Hecho> hechosParseados = parser.parsearHechos(pathCSV);
    for (Hecho h : hechosParseados) {
      h.setPerfil(null);
      h.setAnonimo(false);
    }
    this.hechos = hechosParseados;
  }
}
