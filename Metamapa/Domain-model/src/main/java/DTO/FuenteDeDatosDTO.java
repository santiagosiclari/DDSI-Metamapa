package DTO;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import domain.business.FuentesDeDatos.TipoFuente;
import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.TipoMultimedia;
import java.time.LocalDate;
import java.util.ArrayList;

import lombok.Getter;
import org.javatuples.Pair;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "tipoFuente",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FuenteDemoDTO.class, name = "FUENTEDEMO"),
    @JsonSubTypes.Type(value = FuenteDinamicaDTO.class, name = "FUENTEDINAMICA"),
    @JsonSubTypes.Type(value = FuenteEstaticaDTO.class, name = "FUENTEESTATICA"),
    @JsonSubTypes.Type(value = FuenteMetamapaDTO.class, name = "FUENTEMETAMAPA"),
    @JsonSubTypes.Type(value = FuenteProxyDTO.class, name = "FUENTEPROXY")
})

public abstract class FuenteDeDatosDTO {


  //para pruebas con el repositorio
  //TODO me parece que no va a ir el contadorID, ya que deberia copiar el id de la fuente original
  @Getter
  public Integer id;
  @Getter
  public String nombre;
  @Getter
  public ArrayList<HechoDTO> hechos;
  @Getter
  public TipoFuente tipoFuente;
}