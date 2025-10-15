package Agregador;

import Agregador.Service.ServiceSolicitudes;
import Agregador.Service.ServiceSolicitudes.Result;
import Agregador.DTO.*;
import Agregador.web.ControllerSolicitudes;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import java.math.BigInteger;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerSolicitudesTests {
  private ServiceSolicitudes service;
  private ControllerSolicitudes controller;

  @BeforeEach
  void setUp() {
    service = mock(ServiceSolicitudes.class);
    controller = new ControllerSolicitudes(service);
  }

  @Test
  void obtenerTodasLasSolicitudesEliminacion_conSpamTrue() {
    SolicitudEliminacionDTO dto = new SolicitudEliminacionDTO();
    dto.setId(1);
    dto.setHechoAfectado(BigInteger.ONE);
    when(service.obtenerTodasSolicitudesEliminacion(true)).thenReturn(List.of(dto));

    var response = controller.obtenerTodasLasSolicitudesEliminacion(true);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals(BigInteger.ONE, response.getBody().get(0).getHechoAfectado());
  }

  @Test
  void obtenerTodasLasSolicitudesEliminacion_conSpamFalse() {
    SolicitudEliminacionDTO dto = new SolicitudEliminacionDTO();
    dto.setId(2);
    dto.setHechoAfectado(BigInteger.TEN);
    when(service.obtenerTodasSolicitudesEliminacion(false)).thenReturn(List.of(dto));

    var response = controller.obtenerTodasLasSolicitudesEliminacion(false);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals(BigInteger.TEN, response.getBody().get(0).getHechoAfectado());
  }

  @Test
  void subirSolicitudEliminacion_devuelveCreated() {
    SolicitudEliminacionDTO dto = new SolicitudEliminacionDTO();
    dto.setHechoAfectado(BigInteger.ONE);
    dto.setMotivo("Prueba");
    when(service.crearSolicitud(any(SolicitudEliminacionDTO.class))).thenReturn(dto);

    var response = controller.subirSolicitudEliminacion(dto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(dto, response.getBody());
    assertNotNull(response.getBody());
    assertEquals(BigInteger.ONE, response.getBody().getHechoAfectado());
    assertEquals("Prueba", response.getBody().getMotivo());
  }

  @Test
  void obtenerSolicitud_devuelveDTO() {
    SolicitudEliminacionDTO dto = new SolicitudEliminacionDTO();
    dto.setId(1);
    dto.setHechoAfectado(BigInteger.ONE);
    when(service.buscarPorId(1)).thenReturn(dto);

    var response = controller.obtenerSolicitud(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto, response.getBody());
    assertNotNull(response.getBody());
    assertEquals(BigInteger.ONE, response.getBody().getHechoAfectado());
  }

  @Test
  void actualizarEstadoSolicitud_aprobar_OK() {
    AccionSolicitudDTO dto = new AccionSolicitudDTO();
    dto.setAccion("APROBAR");
    when(service.aprobar(1)).thenReturn(Result.OK);

    var response = controller.actualizarEstadoSolicitud(1, dto);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void actualizarEstadoSolicitud_rechazar_CONFLICT() {
    AccionSolicitudDTO dto = new AccionSolicitudDTO();
    dto.setAccion("RECHAZAR");
    when(service.rechazar(1)).thenReturn(Result.CONFLICT);

    var response = controller.actualizarEstadoSolicitud(1, dto);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  void obtenerTodasLasSolicitudesEdicion_devuelveLista() {
    SolicitudEdicionDTO dto = new SolicitudEdicionDTO();
    dto.setId(1);
    dto.setHechoAfectado(BigInteger.ONE);
    when(service.obtenerTodasSolicitudesEdicion()).thenReturn(List.of(dto));

    var response = controller.obtenerTodasLasSolicitudesEdicion();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals(BigInteger.ONE, response.getBody().get(0).getHechoAfectado());
  }

  @Test
  void obtenerSolicitudEdicionPorId_devuelveDTO() {
    SolicitudEdicionDTO dto = new SolicitudEdicionDTO();
    dto.setId(5);
    dto.setHechoAfectado(BigInteger.TEN);
    when(service.obtenerSolicitudEdicionPorId(5)).thenReturn(dto);

    var response = controller.obtenerSolicitudEdicionPorId(5);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto, response.getBody());
    assertNotNull(response.getBody());
    assertEquals(BigInteger.TEN, response.getBody().getHechoAfectado());
  }

  @Test
  void subirSolicitudEdicion_devuelveCreated() {
    SolicitudEdicionDTO dto = new SolicitudEdicionDTO();
    dto.setId(10);
    dto.setHechoAfectado(BigInteger.valueOf(10));
    dto.setTituloMod("Titulo prueba");
    when(service.crearSolicitudEdicion(any(SolicitudEdicionDTO.class))).thenReturn(dto);

    var response = controller.subirSolicitudEdicion(dto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(dto, response.getBody());
    assertNotNull(response.getBody());
    assertEquals(BigInteger.valueOf(10), response.getBody().getHechoAfectado());
    assertEquals("Titulo prueba", response.getBody().getTituloMod());
  }

  @Test
  void actualizarEstadoSolicitudEdicion_OK() {
    SolicitudEdicionDTO dto = new SolicitudEdicionDTO();
    dto.setId(12);
    dto.setHechoAfectado(BigInteger.valueOf(12));
    when(service.actualizarEstadoSolicitudEdicion(eq(12), anyMap())).thenReturn(dto);

    var response = controller.actualizarEstadoSolicitudEdicion(12, Map.of("estado", "APROBADA"));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto, response.getBody());
    assertNotNull(response.getBody());
    assertEquals(BigInteger.valueOf(12), response.getBody().getHechoAfectado());
  }
}