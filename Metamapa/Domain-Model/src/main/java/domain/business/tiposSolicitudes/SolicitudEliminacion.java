package domain.business.tiposSolicitudes;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SolicitudEliminacion")
public class SolicitudEliminacion extends Solicitud {

  @Column(name = "motivo", length = 255, nullable = false)
  private String motivo;

public SolicitudEliminacion(BigInteger hechoAfectado, String motivo) {
    super(hechoAfectado, EstadoSolicitud.PENDIENTE); //por defecto se inicializan pendientes

    if (motivo == null || motivo.length() < 10) { //TODO: Cambio de 500 a 10
      throw new IllegalArgumentException("El motivo debe tener al menos 10 caracteres.");
    }

    if (DetectorDeSpam.esSpam(motivo)) {
      this.setEstado(EstadoSolicitud.RECHAZADA);
      return;
    }

    this.motivo = motivo;
  }

  @Override
  public void aceptarSolicitud(){
    super.aceptarSolicitud();
   // hechoAfectado.setEliminado(true);
  }
  public void rechazarSolicitud(){
    super.rechazarSolicitud();
  }
}