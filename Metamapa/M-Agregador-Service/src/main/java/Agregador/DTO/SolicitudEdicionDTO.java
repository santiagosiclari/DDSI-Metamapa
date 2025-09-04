package Agregador.DTO;
import Agregador.business.Hechos.Multimedia;
import Agregador.business.Solicitudes.SolicitudEdicion;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolicitudEdicionDTO {
  private BigInteger hechoAfectado;
  private String estado;
  private String tituloMod;
  private String descMod;
  private String categoriaMod;
  private Float latitudMod;
  private Float longitudMod;
  private LocalDate fechaHechoMod;
  private ArrayList<Multimedia> multimediaMod;
  private Boolean anonimidadMod;
  private String sugerencia;
  private int id;

  public SolicitudEdicionDTO() {}

  public SolicitudEdicionDTO(SolicitudEdicion solicitudEdicion) {
    this.tituloMod = solicitudEdicion.getTituloMod();
    this.descMod = solicitudEdicion.getDescMod();
    this.categoriaMod = solicitudEdicion.getCategoriaMod();
    this.latitudMod = solicitudEdicion.getLatitudMod();
    this.longitudMod = solicitudEdicion.getLongitudMod();
    this.fechaHechoMod = solicitudEdicion.getFechaHechoMod();
    this.multimediaMod = solicitudEdicion.getMultimediaMod();
    this.anonimidadMod = solicitudEdicion.getAnonimidadMod();
    this.sugerencia = solicitudEdicion.getSugerencia();
    this.hechoAfectado = solicitudEdicion.getHechoAfectado();
    this.estado = solicitudEdicion.getEstado().name();  // Estado como String
    this.id = solicitudEdicion.getId();
  }
}