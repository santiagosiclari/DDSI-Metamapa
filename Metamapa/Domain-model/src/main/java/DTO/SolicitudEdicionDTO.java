package DTO;
import domain.business.incidencias.Multimedia;
import domain.business.incidencias.Ubicacion;
import java.util.Date;
import java.util.List;
import domain.business.tiposSolicitudes.SolicitudEdicion;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter @Setter
@JsonInclude(Include.NON_NULL)
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
  private int id;

  public SolicitudEdicionDTO() {
  }
  public SolicitudEdicionDTO(SolicitudEdicion solicitudEdicion) {
    this.tituloMod = solicitudEdicion.getTituloMod();
    this.descMod = solicitudEdicion.getDescMod();
    this.categoriaMod = solicitudEdicion.getCategoriaMod();
    this.ubicacionMod = solicitudEdicion.getUbicacionMod();
    this.fechaHechoMod = solicitudEdicion.getFechaHechoMod();
    this.multimediaMod = solicitudEdicion.getMultimediaMod();
    this.anonimidadMod = solicitudEdicion.getAnonimidadMod();
    this.sugerencia = solicitudEdicion.getSugerencia();
    this.hechoAfectado = solicitudEdicion.getHechoAfectado();
    this.estado = solicitudEdicion.getEstado().name();  // Estado como String
    this.id = solicitudEdicion.getId();
  }
}
