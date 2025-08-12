package FuenteEstatica.business.FuentesDeDatos;
import FuenteEstatica.business.Parsers.CSVHechoParser;
import FuenteEstatica.business.Hechos.Hecho;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
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

  //public void agregarHecho(){throw new UnsupportedOperationException("Not supported yet.");};

  //TODO Agregar un metodo que actualice un hecho. y luego implementar en el metodo cargarCSV asi si matchea el titulo actualice el hecho con los datos nuevos.

  public void agregarHecho(Hecho h) {
    Optional<Hecho> duplicado = hechos.stream().filter(hd -> Objects.equals(hd.getTitulo(), h.getTitulo())).findFirst();
    if (duplicado.isEmpty()) this.hechos.add(h);
    else
      duplicado.get().editarHecho(h.getDescripcion(), h.getCategoria(), h.getUbicacion().getLatitud(), h.getUbicacion().getLongitud(), h.getFechaHecho());
  }


  public void cargarCSV(String path) {
    new CSVHechoParser().parsearHechos(path, id).forEach((this::agregarHecho));
  }

}
