package domain.business.FuentesDeDatos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import domain.business.Parsers.HechoParser;
import domain.business.incidencias.Hecho;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@JsonTypeName("FUENTEESTATICA")

public class FuenteEstatica extends FuenteDeDatos{

  //@Getter
  //private String pathCSV;

  @Getter @Setter
  private HechoParser parser;
  public FuenteEstatica() {} // va a haber que usar dtos para no modificar la capa de negocio
  public FuenteEstatica( String nombre){
    this.nombre = nombre;
    this.id = contadorID++;
   // this.pathCSV = pathCSV;
    this.hechos = new ArrayList<>();
    this.tipoFuente = tipoFuente.FUENTEESTATICA;
  }

  public void agregarHecho(){throw new UnsupportedOperationException("Not supported yet.");};

  //TODO Agregar un metodo que actualice un hecho. y luego implementar en el metodo cargarCSV asi si matchea el titulo actualice el hecho con los datos nuevos.

  public void cargarCSV(String pathCSV){
    ArrayList<Hecho> hechosParseados = parser.parsearHechos(pathCSV);
    for (Hecho h : hechosParseados) {
      h.setPerfil(null);
      h.setAnonimo(false);
      h.setFuenteId(this.id);
      //this.hechos.removeIf(j -> j.getTitulo().equals(h.getTitulo()));
      this.hechos.add(h);
      }
    }
}
