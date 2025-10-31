package Agregador;
import Agregador.Service.ServiceSolicitudes;
import Agregador.Service.ServiceSolicitudes.Result;
import Agregador.business.Hechos.*;
import Agregador.business.Solicitudes.*;
import Agregador.persistencia.*;
import Agregador.DTO.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ServiceSolicitudesTests {
  @Mock
  private RepositorioSolicitudesEliminacion repoEliminacion;
  @Mock
  private RepositorioSolicitudesEdicion repoEdicion;
  @Mock
  private RepositorioHechos repoHechos;

  @InjectMocks
  private ServiceSolicitudes service;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  // ---------- Helpers ----------
  private Hecho mockHecho(long id, LocalDate fechaCarga) {
    Hecho hecho = mock(Hecho.class);
    when(hecho.getId()).thenReturn(BigInteger.valueOf(id));
    when(hecho.getFechaCarga()).thenReturn(fechaCarga.atStartOfDay());
    return hecho;
  }

  private SolicitudEliminacion mockSolicitudEliminacion(EstadoSolicitud estado, Hecho hecho) {
    SolicitudEliminacion s = new SolicitudEliminacion();
    s.setHechoAfectado(hecho);
    s.setEstado(estado);
    return s;
  }

  private SolicitudEdicion mockSolicitudEdicion(Hecho hecho) {
    SolicitudEdicion s = new SolicitudEdicion();
    s.setHechoAfectado(hecho);
    s.setEstado(EstadoSolicitud.PENDIENTE);
    return s;
  }

  // ---------- aprobar / rechazar ----------
  @Test
  void aprobar_devuelveOK_yGuardaSolicitud() {
    Hecho hecho = mockHecho(1, LocalDate.now());
    SolicitudEliminacion s = mockSolicitudEliminacion(EstadoSolicitud.PENDIENTE, hecho);
    when(repoEliminacion.findById(1)).thenReturn(Optional.of(s));
    Result result = service.aprobar(1);

    assertEquals(Result.OK, result);
    assertEquals(EstadoSolicitud.APROBADA, s.getEstado());
    verify(repoEliminacion).save(s);
  }

  @Test
  void aprobar_devuelveCONFLICT_siNoPendiente() {
    Hecho hecho = mockHecho(1, LocalDate.now());
    SolicitudEliminacion s = mockSolicitudEliminacion(EstadoSolicitud.APROBADA, hecho);
    when(repoEliminacion.findById(1)).thenReturn(Optional.of(s));
    Result result = service.aprobar(1);

    assertEquals(Result.CONFLICT, result);
    verify(repoEliminacion, never()).save(any());
  }

  @Test
  void rechazar_devuelveOK_yGuardaSolicitud() {
    Hecho hecho = mockHecho(2, LocalDate.now());
    SolicitudEliminacion s = mockSolicitudEliminacion(EstadoSolicitud.PENDIENTE, hecho);
    when(repoEliminacion.findById(2)).thenReturn(Optional.of(s));
    Result result = service.rechazar(2);

    assertEquals(Result.OK, result);
    verify(repoEliminacion).save(s);
  }

  @Test
  void rechazar_lanzaExcepcion_siNoExiste() {
    when(repoEliminacion.findById(3)).thenReturn(Optional.empty());
    assertThrows(NoSuchElementException.class, () -> service.rechazar(3));
  }

  @Test
  void crearSolicitud_guardaYDevuelveDTO() {
    SolicitudEliminacionDTO dto = new SolicitudEliminacionDTO();
    dto.setHechoAfectado(BigInteger.valueOf(1));
    dto.setMotivo("Prueba");
    Hecho hecho = mockHecho(1, LocalDate.now());
    when(repoHechos.findById(BigInteger.valueOf(1))).thenReturn(Optional.of(hecho));
    SolicitudEliminacionDTO result = service.crearSolicitud(dto);
    assertNotNull(result);
    verify(repoEliminacion).save(any(SolicitudEliminacion.class));
  }

  @Test
  void crearSolicitud_lanzaExcepcion_siHechoNoExiste() {
    SolicitudEliminacionDTO dto = new SolicitudEliminacionDTO();
    dto.setHechoAfectado(BigInteger.valueOf(10));
    when(repoHechos.findById(BigInteger.valueOf(10))).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> service.crearSolicitud(dto));
  }

  @Test
  void buscarPorId_devuelveDTO_siExiste() {
    Hecho hecho = mockHecho(1, LocalDate.now());
    SolicitudEliminacion s = mockSolicitudEliminacion(EstadoSolicitud.PENDIENTE, hecho);
    when(repoEliminacion.findById(1)).thenReturn(Optional.of(s));
    SolicitudEliminacionDTO dto = service.buscarPorId(1);

    assertNotNull(dto);
    assertEquals(BigInteger.valueOf(1), dto.getHechoAfectado());
    verify(repoEliminacion).findById(1);
  }

  @Test
  void buscarPorId_lanzaExcepcion_siNoExiste() {
    when(repoEliminacion.findById(1)).thenReturn(Optional.empty());
    assertThrows(NoSuchElementException.class, () -> service.buscarPorId(1));
  }

  @Test
  void obtenerTodasSolicitudesEliminacion_conSpamTrue_llamaFindByEstado() {
    Hecho hecho = mockHecho(1, LocalDate.now());
    SolicitudEliminacion s = mockSolicitudEliminacion(EstadoSolicitud.SPAM, hecho);
    when(repoEliminacion.findByEstado(EstadoSolicitud.SPAM)).thenReturn(List.of(s));
    List<SolicitudEliminacionDTO> result = service.obtenerTodasSolicitudesEliminacion(true);

    assertEquals(1, result.size());
    assertEquals(EstadoSolicitud.SPAM, result.get(0).getEstado());
    verify(repoEliminacion).findByEstado(EstadoSolicitud.SPAM);
  }

  @Test
  void obtenerTodasSolicitudesEliminacion_conSpamFalse_llamaFindAllWhereNot() {
    Hecho hecho = mockHecho(1, LocalDate.now());
    SolicitudEliminacion s = mockSolicitudEliminacion(EstadoSolicitud.PENDIENTE, hecho);
    when(repoEliminacion.findAllWhereEstadoNot(EstadoSolicitud.SPAM)).thenReturn(List.of(s));
    List<SolicitudEliminacionDTO> dtos = service.obtenerTodasSolicitudesEliminacion(false);

    assertEquals(1, dtos.size());
    assertEquals(BigInteger.valueOf(1), dtos.get(0).getHechoAfectado());
  }

  @Test
  void obtenerTodasSolicitudesEliminacion_conSpamNull_llamaFindAll() {
    Hecho hecho1 = mockHecho(1, LocalDate.now());
    Hecho hecho2 = mockHecho(2, LocalDate.now());
    SolicitudEliminacion s1 = mockSolicitudEliminacion(EstadoSolicitud.PENDIENTE, hecho1);
    SolicitudEliminacion s2 = mockSolicitudEliminacion(EstadoSolicitud.APROBADA, hecho2);
    when(repoEliminacion.findAll()).thenReturn(List.of(s1, s2));
    List<SolicitudEliminacionDTO> result = service.obtenerTodasSolicitudesEliminacion(null);

    assertEquals(2, result.size());
    verify(repoEliminacion).findAll();
  }
  @Test
  void obtenerTodasSolicitudesEdicion_devuelveListaDTO() {
    Hecho hecho1 = mock(Hecho.class);
    when(hecho1.getId()).thenReturn(BigInteger.valueOf(1));
    Hecho hecho2 = mock(Hecho.class);
    when(hecho2.getId()).thenReturn(BigInteger.valueOf(2));
    SolicitudEdicion s1 = mockSolicitudEdicion(hecho1);
    s1.setId(1);
    SolicitudEdicion s2 = mockSolicitudEdicion(hecho2);
    s2.setId(2);
    when(repoEdicion.findAll()).thenReturn(List.of(s1, s2));

    List<SolicitudEdicionDTO> dtos = service.obtenerTodasSolicitudesEdicion();

    assertEquals(2, dtos.size());
    assertEquals(BigInteger.valueOf(1), dtos.get(0).getHechoAfectado());
    assertEquals(BigInteger.valueOf(2), dtos.get(1).getHechoAfectado());
    verify(repoEdicion).findAll();
  }

  @Test
  void obtenerSolicitudEdicionPorId_devuelveDTO_siExiste() {
    Hecho hecho = mock(Hecho.class);
    when(hecho.getId()).thenReturn(BigInteger.valueOf(1));
    SolicitudEdicion s = mockSolicitudEdicion(hecho);
    s.setId(1);
    when(repoEdicion.findById(1)).thenReturn(Optional.of(s));
    SolicitudEdicionDTO dto = service.obtenerSolicitudEdicionPorId(1);

    assertNotNull(dto);
    assertEquals(BigInteger.valueOf(1), dto.getHechoAfectado());
    verify(repoEdicion).findById(1);
  }

  @Test
  void obtenerSolicitudEdicionPorId_lanzaExcepcion_siNoExiste() {
    when(repoEdicion.findById(1)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> service.obtenerSolicitudEdicionPorId(1));
    verify(repoEdicion).findById(1);
  }



  @Test
  void crearSolicitudEdicion_ok_siMenosDe7Dias() {
    SolicitudEdicionDTO dto = new SolicitudEdicionDTO();
    dto.setHechoAfectado(BigInteger.valueOf(1));
    Hecho hecho = mockHecho(1, LocalDate.now().minusDays(5));
    when(repoHechos.findById(BigInteger.valueOf(1))).thenReturn(Optional.of(hecho));
    SolicitudEdicionDTO result = service.crearSolicitudEdicion(dto);

    assertNotNull(result);
    verify(repoEdicion).save(any(SolicitudEdicion.class));
  }

  @Test
  void crearSolicitudEdicion_lanzaExcepcion_siMasDe7Dias() {
    SolicitudEdicionDTO dto = new SolicitudEdicionDTO();
    dto.setHechoAfectado(BigInteger.valueOf(1));
    Hecho hecho = mockHecho(1, LocalDate.now().minusDays(10));
    when(repoHechos.findById(BigInteger.valueOf(1))).thenReturn(Optional.of(hecho));

    assertThrows(IllegalArgumentException.class, () -> service.crearSolicitudEdicion(dto));
  }

  @Test
  void actualizarEstadoSolicitudEdicion_apruebaSolicitud() {
    Hecho hecho = mockHecho(1, LocalDate.now());
    SolicitudEdicion s = mockSolicitudEdicion(hecho);
    when(repoEdicion.findById(1)).thenReturn(Optional.of(s));
    Map<String, Object> request = Map.of("estado", "APROBADA");
    service.actualizarEstadoSolicitudEdicion(1, request);

    assertEquals(EstadoSolicitud.APROBADA, s.getEstado());
  }

  @Test
  void actualizarEstadoSolicitudEdicion_rechazaSolicitud() {
    Hecho hecho = mockHecho(1, LocalDate.now());
    SolicitudEdicion s = mockSolicitudEdicion(hecho);
    when(repoEdicion.findById(1)).thenReturn(Optional.of(s));
    Map<String, Object> requestBody = Map.of("estado", "RECHAZADA");
    service.actualizarEstadoSolicitudEdicion(1, requestBody);

    assertEquals(EstadoSolicitud.RECHAZADA, s.getEstado());
    verify(repoEdicion).save(s);
  }

  @Test
  void actualizarEstadoSolicitudEdicion_lanzaExcepcion_siEstadoInvalido() {
    Hecho hecho = mockHecho(1, LocalDate.now());
    when(repoEdicion.findById(1)).thenReturn(Optional.of(mockSolicitudEdicion(hecho)));

    Map<String, Object> body = Map.of("estado", "PENDIENTE");
    assertThrows(IllegalArgumentException.class, () -> service.actualizarEstadoSolicitudEdicion(1, body));
  }
}