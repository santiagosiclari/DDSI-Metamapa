package DTO;
import domain.business.incidencias.Multimedia;
import domain.business.incidencias.Ubicacion;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitudEdicionDTO {
  private String hechoAfectado;
  private String estado;
  private String tituloMod;
  private String descMod;
  private String categoriaMod;
  private Ubicacion ubicacionMod;
  private Date fechaHechoMod;
  private List<Multimedia> multimediaMod;
  private Boolean anonimidadMod;
  private String sugerencia;
}
