package domain.business.tiposSolicitudes;

import domain.business.incidencias.Multimedia;
import domain.business.incidencias.Ubicacion;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SolicitudEdicion")
public class SolicitudEdicion extends Solicitud {

  @Column(name = "tituloMod", length = 255)
  private String tituloMod;

  @Column(name = "descMod", length = 255)
  private String descMod;

  // El DER pide latitud/longitud modificadas, no un objeto Ubicacion
  @Column(name = "latitudMod", precision = 12, scale = 2)
  private BigDecimal latitudMod;

  @Column(name = "longitudMod", precision = 12, scale = 2)
  private BigDecimal longitudMod;

  @Column(name = "fechaHechoMod")
  private LocalDate fechaHechoMod;

  // En el DER figura "anonimidad"
  @Column(name = "anonimidad")
  private Boolean anonimidad;

  @Column(name = "sugerencia", length = 255)
  private String sugerencia;

public SolicitudEdicion(String tituloMod,
                          String descMod,
                          LocalDate fechaHechoMod,
                          String sugerencia,
                          BigInteger hechoAfectado) {
    super(hechoAfectado, EstadoSolicitud.PENDIENTE);
    this.tituloMod = tituloMod;
    this.descMod = descMod;
    this.fechaHechoMod = fechaHechoMod;
    this.sugerencia = sugerencia;
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