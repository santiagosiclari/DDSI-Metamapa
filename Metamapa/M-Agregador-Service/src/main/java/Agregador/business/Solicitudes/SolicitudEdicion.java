package Agregador.business.Solicitudes;
import java.time.LocalDateTime;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class SolicitudEdicion extends Solicitud {
  private String tituloMod;
  private String descMod;
  private String categoriaMod;
  private Float latitudMod;
  private Float longitudMod;
  private LocalDateTime fechaHechoMod;
  private Boolean anonimidadMod;
  private String sugerencia;

  public SolicitudEdicion(String tituloMod,
                          String descMod,
                          String categoriaMod,
                          Float latitudMod,
                          Float longitudMod,
                          LocalDateTime fechaHechoMod,
                          Boolean anonimidadMod,
                          String sugerencia,
                          Hecho hechoAfectado) {
    super(hechoAfectado, EstadoSolicitud.PENDIENTE);
    this.tituloMod = tituloMod;
    this.descMod = descMod;
    this.categoriaMod = categoriaMod;
    this.latitudMod = latitudMod;
    this.longitudMod = longitudMod;
    this.fechaHechoMod = fechaHechoMod;
    this.anonimidadMod = anonimidadMod;
    this.sugerencia = sugerencia;
  }

  public SolicitudEdicion() {}

  public void agregarSugerencia(String sugerencia) {
    this.sugerencia = sugerencia;
  }

  @Override
  public void aceptarSolicitud() {
    super.aceptarSolicitud();
    getHechoAfectado().editarHecho(
            this.tituloMod,
            this.descMod,
            this.categoriaMod,
            this.latitudMod,
            this.longitudMod,
            this.fechaHechoMod,
            this.anonimidadMod);
  }

  public void rechazarSolicitud() {
    super.rechazarSolicitud();
  }
}