package Metamapa.business.FuentesDeDatos;
import Metamapa.business.Hechos.*;
import Metamapa.business.Usuarios.Usuario;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import java.util.ArrayList;
import lombok.Getter;

@JsonTypeName("FUENTEDINAMICA")
public class FuenteDinamica {
  static private Integer contadorID = 1000000;
  @Getter
  public String nombre;
  @Getter
  ArrayList<Hecho> hechos;
  @Getter
  Integer fuenteId;

  public FuenteDinamica() {
    if (contadorID > 1999998) {
      throw new RuntimeException("No hay mas espacio para nuevas fuentes :(");
    } else {
      this.fuenteId = contadorID++;
      this.nombre = "Fuente Dinamica";
      this.hechos = new ArrayList<>();
    }
  }

  public void agregarHecho(
      String titulo,
      String desc,
      String categoria,
      Float latitud,
      Float longitud,
      LocalDate fechaHecho,
      Integer idAutor,
      Boolean anonimidad,
      ArrayList<Multimedia> multimedia) {

    this.hechos.add(new Hecho(
            titulo,
            desc,
            categoria,
            latitud,
            longitud,
            fechaHecho,
            new Usuario("san", "sa", "sa", "sa", 16, null), //idAutor,
            this.fuenteId,
            0,
            anonimidad,
            multimedia
        )
    );
  }
}