package domain.business.FuentesDeDatos;
import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import lombok.Getter;
import java.util.List;
public class FuenteDinamica extends FuenteDeDatos{

  public FuenteDinamica() {
    this.id = contadorID++;
    this.nombre = "Fuente Dinamica";
    this.hechos =  new ArrayList<>();
  }

  public void agregarHecho(String titulo, String desc, String categoria, Float latitud, Float longitud, LocalDate fechaHecho,
                           Perfil autor, Boolean anonimidad, ArrayList<Multimedia> multimedia) {
    this.hechos.add(new Hecho(titulo,desc,categoria,latitud,longitud,fechaHecho,autor,anonimidad,multimedia));
  }
}