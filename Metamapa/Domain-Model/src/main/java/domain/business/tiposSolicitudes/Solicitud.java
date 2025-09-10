package domain.business.tiposSolicitudes;

import domain.business.Usuarios.Usuario;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "Solicitud")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Solicitud {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "solicitud_Id")
  private Long id;

  // FK a Hecho
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "hechoAfectado", nullable = false)
  private Hecho hechoAfectado;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", length = 20, nullable = false)
  private EstadoSolicitud estado;

  // FK a Usuario
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuarioID", nullable = false)
  private Usuario usuario;

  public Solicitud(BigInteger hechoAfectado, EstadoSolicitud estado) {

  }

  // Helpers de dominio
  public void aceptarSolicitud() { this.estado = EstadoSolicitud.APROBADA; }
  public void rechazarSolicitud() { this.estado = EstadoSolicitud.RECHAZADA; }
}
