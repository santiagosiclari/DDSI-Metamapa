package Agregador;
import Agregador.DTO.*;
import Agregador.Service.ServiceColecciones;
import Agregador.business.Consenso.ModosDeNavegacion;
import Agregador.business.Hechos.Hecho;
import Agregador.web.ControllerColecciones;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerColeccionesTests {
  private ServiceColecciones service;
  private ControllerColecciones controller;
  private UUID id;

  @BeforeEach
  void setUp() {
    service = mock(ServiceColecciones.class);
    controller = new ControllerColecciones(service);
    id = UUID.randomUUID();
  }

  @Test
  void obtenerTodasLasColecciones_devuelveLista() {
    List<ColeccionDTO> colecciones = List.of(new ColeccionDTO("C1", "D1", "Mayoría simple", List.of()));
    when(service.obtenerTodasLasColecciones()).thenReturn(colecciones);
    var response = controller.obtenerTodasLasColecciones();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(colecciones, response.getBody());
  }

  @Test
  void obtenerColeccionPorId_devuelveDTO() {
    ColeccionDTO dto = new ColeccionDTO("C1", "D1", "Mayoría simple", List.of());
    when(service.obtenerColeccionPorId(id)).thenReturn(dto);
    var response = controller.obtenerColeccionPorId(id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto, response.getBody());
  }

  @Test
  void crearColeccion_devuelve201() {
    ColeccionDTO dto = new ColeccionDTO("Colección 1", "Desc", "Mayoría simple", List.of());
    when(service.crearColeccion(any(ColeccionDTO.class))).thenReturn(dto);
    var response = controller.crearColeccion(dto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Colección 1", response.getBody().getTitulo());
  }

  @Test
  void actualizarColeccion_devuelveDTO() {
    ColeccionDTO dto = new ColeccionDTO("C1", "Desc", "Mayoría simple", List.of());
    when(service.actualizarColeccion(eq(id), any(ColeccionDTO.class))).thenReturn(dto);
    var response = controller.actualizarColeccion(id, dto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto, response.getBody());
  }

  @Test
  void modificarAlgoritmo_devuelveNoContent() {
    doNothing().when(service).modificarAlgoritmo(eq(id), anyMap());
    var response = controller.modificarAlgoritmo(id, Map.of("consenso", "Unanimidad"));

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());  }

  @Test
  void eliminarColeccion_devuelveNoContent() {
    doNothing().when(service).eliminarColeccion(id);
    var response = controller.eliminarColeccion(id);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void agregarFuente_devuelveOk() {
    doNothing().when(service).agregarFuenteDeDatos(id, 1);
    var response = controller.agregarFuente(id, 1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Fuente agregada a la colección", response.getBody());
  }

  @Test
  void eliminarFuente_devuelveNoContent() {
    doNothing().when(service).eliminarFuenteDeDatos(id, 1);
    var response = controller.eliminarFuente(id, 1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void getHechosColeccion_devuelveLista() {
    List<Hecho> hechos = List.of(new Hecho());
    FiltrosHechosDTO filtros = new FiltrosHechosDTO();
    when(service.getHechosFiltrados(eq(id), eq(ModosDeNavegacion.IRRESTRICTA), eq(filtros))).thenReturn(hechos);
    var response = controller.getHechosColeccion(id, ModosDeNavegacion.IRRESTRICTA, filtros);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(hechos, response.getBody());
  }
}
