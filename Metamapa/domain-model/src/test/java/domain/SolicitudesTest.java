/*
package domain.business.tiposSolicitudes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SolicitudEliminacionTest {

  @Test
  void crearSolicitud_validaElEstadoInicial() {
    SolicitudEliminacion s = new SolicitudEliminacion("hecho1", "Motivo válido largo");
    assertEquals(EstadoSolicitud.PENDIENTE, s.getEstado());
  }

  @Test
  void aceptarSolicitud_cambiaAprobada() {
    SolicitudEliminacion s = new SolicitudEliminacion("hecho1", "Motivo válido largo");
    s.aceptarSolicitud();
    assertEquals(EstadoSolicitud.APROBADA, s.getEstado());
  }

  @Test
  void rechazarSolicitud_cambiaRechazada() {
    SolicitudEliminacion s = new SolicitudEliminacion("hecho1", "Motivo válido largo");
    s.rechazarSolicitud();
    assertEquals(EstadoSolicitud.RECHAZADA, s.getEstado());
  }

  @Test
  void motivoCorto_lanzaExcepcion() {
    assertThrows(IllegalArgumentException.class,
        () -> new SolicitudEliminacion("h", "corto"));
  }
}
*/