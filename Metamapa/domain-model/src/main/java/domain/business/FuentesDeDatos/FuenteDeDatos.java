package domain.business.FuentesDeDatos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.TipoMultimedia;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.UUID;
import lombok.Getter;
import org.javatuples.Pair;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "tipoFuente",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FuenteDemo.class, name = "FUENTEDEMO"),
    @JsonSubTypes.Type(value = FuenteDinamica.class, name = "FUENTEDINAMICA"),
    @JsonSubTypes.Type(value = FuenteEstatica.class, name = "FUENTEESTATICA"),
    @JsonSubTypes.Type(value = FuenteMetamapa.class, name = "FUENTEMETAMAPA"),
    @JsonSubTypes.Type(value = FuenteProxy.class, name = "FUENTEPROXY")
})

public abstract class FuenteDeDatos {
  //para pruebas con el repositorio
  @Getter
  static protected  Integer contadorID = 1;
  @Getter
  protected Integer id;
  @Getter
  public String nombre;
  @Getter
  public ArrayList<Hecho> hechos;
  @Getter
  public TipoFuente tipoFuente;

  public void agregarHecho(String titulo, String descripcion, String categoria, Float latitud, Float longitud, LocalDate fechaHecho , Perfil autor, Boolean anonimo, Boolean eliminado, ArrayList<Pair<TipoMultimedia,String>> multimedia)
   {
   }

  public void agregarHecho(ArrayList<Hecho> hechos) {
  }
 public void agregarHecho(Hecho hecho) {}
}


/*  void agregarHechosParser(ArrayList<HechoDTO> hechos){
    hechos.stream().map(h->agregarHecho(h.getTitulo(), h.getDescripcion(), h.getCategoria(), h.getUbicacion().getLatitud(), h.getUbicacion().getLongitud(), h.getFechaHecho(),))
  }
}*/