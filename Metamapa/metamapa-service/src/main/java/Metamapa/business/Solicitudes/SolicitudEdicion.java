package Metamapa.business.Solicitudes;

import Metamapa.business.Hechos.Multimedia;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import lombok.Getter;

public class SolicitudEdicion extends Solicitud {
  @Getter
  private String tituloMod;
  @Getter
  private String descMod;
  @Getter
  private String categoriaMod;
  @Getter
  private Float latitudMod;
  @Getter
  private Float longitudMod;
  @Getter
  private LocalDate fechaHechoMod;
  @Getter
  private ArrayList<Multimedia> multimediaMod;
  @Getter
  private Boolean anonimidadMod;
  @Getter
  private String sugerencia;
  static private Integer contadorID = 1;

  public SolicitudEdicion(String tituloMod,
                          String descMod,
                          String categoriaMod,
                          Float latitudMod,
                          Float longitudMod,
                          LocalDate fechaHechoMod,
                          ArrayList<Multimedia> multimediaMod,
                          Boolean anonimidadMod,
                          String sugerencia,
                          BigInteger hechoAfectado) {
    super(hechoAfectado, EstadoSolicitud.PENDIENTE);
    /*
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