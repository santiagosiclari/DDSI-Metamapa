package domain.business.FuentesDeDatos;
import com.fasterxml.jackson.annotation.JsonTypeName;
import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@JsonTypeName("FUENTEDINAMICA")
public class FuenteDinamica extends FuenteDeDatos{

  public FuenteDinamica() {
    this.id = contadorID++;
    this.nombre = "Fuente Dinamica";
    this.hechos =  new ArrayList<>();
    this.tipoFuente = tipoFuente.FUENTEDINAMICA;
  }
  public void agregarHecho(Hecho hecho){
    hechos.add(hecho);
  }
  public void agregarHecho(String titulo, String desc, String categoria, Float latitud, Float longitud, LocalDate fechaHecho,
                           Perfil autor, Boolean anonimidad, ArrayList<Multimedia> multimedia) {
    this.hechos.add(new Hecho(titulo,desc,categoria,latitud,longitud,fechaHecho,autor,this.id ,anonimidad,multimedia));
  }
}