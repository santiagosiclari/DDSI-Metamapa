package Agregador.business.Solicitudes;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import Agregador.business.Hechos.Multimedia;

@Getter
public class SolicitudEdicion extends Solicitud {
  private String tituloMod;
  private String descMod;
  private String categoriaMod;
  private Float latitudMod;
  private Float longitudMod;
  private Date fechaHechoMod;
  private List<Multimedia> multimediaMod;
  private Boolean anonimidadMod;
  private String sugerencia;
  static private Integer contadorID = 1;
  public SolicitudEdicion(String tituloMod,
                          String descMod,
                          String categoriaMod,
                          Float latitudMod,
                          Float longitudMod,
                          Date fechaHechoMod,
                          List<Multimedia> multimediaMod,
                          Boolean anonimidadMod,
                          String sugerencia,
                          String hechoAfectado) {
    super(hechoAfectado, EstadoSolicitud.PENDIENTE);
    /* //TODO: SE VERIFICA EN EL CONTROLLER SOLICITUDESEDICION, AL CREARSE UNA
    if(hechoAfectado.getFechaCarga().plusDays(7).isBefore(LocalDate.now()))
    {
        throw new RuntimeException("Paso mas de una semana de la carga del Hecho");
    }*/
    this.tituloMod = tituloMod;
    this.descMod = descMod;
    this.categoriaMod = categoriaMod;
    this.latitudMod = latitudMod;
    this.longitudMod = longitudMod;
    this.fechaHechoMod = fechaHechoMod;
    this.multimediaMod = multimediaMod;
    this.anonimidadMod = anonimidadMod;
    this.sugerencia = sugerencia;
    this.id = contadorID++;
  }

  public void agregarSugerencia(String sugerencia){
    this.sugerencia = sugerencia;
  }

  @Override
  public void aceptarSolicitud(){
    super.aceptarSolicitud();
    //hechoAfectado.editarHecho(this);
  }
  public void rechazarSolicitud(){
    super.rechazarSolicitud();
  }
}