package domain.business.tiposSolicitudes;
import domain.business.incidencias.Ubicacion;
import domain.business.incidencias.Multimedia;
import domain.business.incidencias.Hecho;
import java.time.LocalDate;
import java.util.List;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

public class SolicitudEdicion extends Solicitud{

  @Getter
  private String tituloMod;
  @Getter
  private String descMod;
  @Getter
  private String categoriaMod;
  @Getter
  private Ubicacion ubicacionMod;
  @Getter
  private Date fechaHechoMod;
  @Getter
  private List<Multimedia> multimediaMod;
  @Getter
  private Boolean anonimidadMod;
  @Getter
  private String sugerencia;

  // Constructor
  public SolicitudEdicion(String tituloMod, String descMod, String categoriaMod, Ubicacion ubicacionMod, Date fechaHechoMod, List<Multimedia> multimediaMod, Boolean anonimidadMod, String sugerencia, Hecho hechoAfectado) {
    super(hechoAfectado, EstadoSolicitud.PENDIENTE);
    if(hechoAfectado.getFechaCarga().plusDays(7).isBefore(LocalDate.now()))
    {
        throw new RuntimeException("Paso mas de una semana de la carga del Hecho");
    }
    this.tituloMod = tituloMod;
    this.descMod = descMod;
    this.categoriaMod = categoriaMod;
    this.ubicacionMod = ubicacionMod;
    this.fechaHechoMod = fechaHechoMod;
    this.multimediaMod = multimediaMod;
    this.anonimidadMod = anonimidadMod;
    this.sugerencia = sugerencia;
  }

  public void agregarSugerencia(String sugerencia){
    this.sugerencia = sugerencia;
  }

  @Override
  public void aceptarSolicitud(){
    super.aceptarSolicitud();
    hechoAfectado.editarHecho(this);
  }
  public void rechazarSolicitud(){
    super.rechazarSolicitud();
  }
}