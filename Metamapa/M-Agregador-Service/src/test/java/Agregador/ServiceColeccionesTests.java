package Agregador;
import Agregador.DTO.*;
import Agregador.Service.ServiceColecciones;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ServiceColeccionesTests {
  @Mock
  private RepositorioColecciones repositorioColecciones;
  @Mock
  private RepositorioHechosImpl repositorioHechos;
  @Mock
  private RepositorioConsenso repositorioConsenso;
  @InjectMocks
  private ServiceColecciones service;
  private Coleccion coleccion;
  private UUID id;
  private Consenso consenso;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    id = UUID.randomUUID();
    consenso = new MayoriaSimple();
    coleccion = new Coleccion("Título", "Descripción", consenso, new ArrayList<>());
  }

  @Test
  void obtenerTodasLasColecciones_devuelveListaDTO() {
    List<Coleccion> coleccionesList = List.of(coleccion);
    when(repositorioColecciones.findAll()).thenReturn(coleccionesList);
    List<ColeccionDTO> result = service.obtenerTodasLasColecciones();

    assertEquals(1, result.size());
    assertEquals("Título", result.get(0).getTitulo());
    verify(repositorioColecciones).findAll();
  }

  @Test
  void obtenerColeccionPorId_devuelveDTO() {
    when(repositorioColecciones.findById(id)).thenReturn(Optional.of(coleccion));
    ColeccionDTO dto = service.obtenerColeccionPorId(id);

    assertNotNull(dto);
    assertEquals("Título", dto.getTitulo());
    verify(repositorioColecciones).findById(id);
  }

  @Test
  void obtenerColeccionPorId_lanzaExcepcion_siNoExiste() {
    when(repositorioColecciones.findById(id)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> service.obtenerColeccionPorId(id));
  }

  @Test
  void crearColeccion_guardaYDevuelveDTO() {
    ColeccionDTO dto = new ColeccionDTO();
    dto.setTitulo("Nueva");
    dto.setDescripcion("Desc");
    dto.setConsenso("Mayoría simple");
    dto.setCriterios(List.of());
    when(repositorioConsenso.findByDescripcion("Mayoría simple"))
            .thenReturn(Optional.of(consenso));
    ColeccionDTO result = service.crearColeccion(dto);

    assertNotNull(result);
    assertEquals("Nueva", result.getTitulo());
    verify(repositorioColecciones).save(any(Coleccion.class));
  }

  @Test
  void eliminarColeccion_lanzaExcepcion_siNoExiste() {
    when(repositorioColecciones.existsById(id)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> service.eliminarColeccion(id));
    verify(repositorioColecciones, never()).deleteById(id);
  }

  @Test
  void eliminarColeccion_eliminaSiExiste() {
    when(repositorioColecciones.existsById(id)).thenReturn(true);
    service.eliminarColeccion(id);

    verify(repositorioColecciones).deleteById(id);
  }

  @Test
  void actualizarColeccion_devuelveDTOActualizada() {
    ColeccionDTO dto = new ColeccionDTO("Título Nuevo", "Desc", "MayoriaSimple", List.of());
    when(repositorioConsenso.findByDescripcion("MayoriaSimple"))
            .thenReturn(Optional.of(consenso));
    when(repositorioColecciones.findById(id)).thenReturn(Optional.of(coleccion));
    when(repositorioColecciones.save(any(Coleccion.class))).thenReturn(coleccion);
    ColeccionDTO result = service.actualizarColeccion(id, dto);

    assertNotNull(result);
    assertEquals("Título Nuevo", result.getTitulo());
    verify(repositorioColecciones).save(any(Coleccion.class));
  }

  @Test
  void modificarAlgoritmo_cambiaConsenso() {
    Map<String, Object> body = Map.of("consenso", "Mayoría simple");
    when(repositorioColecciones.findById(id)).thenReturn(Optional.of(coleccion));
    when(repositorioConsenso.findByDescripcion("Mayoría simple")).thenReturn(Optional.of(consenso));
    service.modificarAlgoritmo(id, body);

    assertEquals(consenso, coleccion.getConsenso());
    verify(repositorioColecciones).save(coleccion);
  }

  @Test
  void getHechosFiltrados_devuelveLista() {
    Hecho hecho = new Hecho();
    List<Hecho> hechosMock = List.of(hecho);
    Coleccion coleccionConCriterios = new Coleccion("T", "D", consenso, new ArrayList<>());
    when(repositorioColecciones.findById(id)).thenReturn(Optional.of(coleccionConCriterios));
    when(repositorioHechos.filtrarPorCriterios(anyList(), any())).thenReturn(hechosMock);
    FiltrosHechosDTO filtrosDTO = new FiltrosHechosDTO();
    List<Hecho> result = service.getHechosFiltrados(id, ModosDeNavegacion.IRRESTRICTA, filtrosDTO);
    assertEquals(1, result.size());
    assertEquals(hecho, result.get(0));
  }
  @Test
  void getHechosFiltrados_curada_aplicaConsenso() {
    FiltrosHechosDTO filtros = new FiltrosHechosDTO();
    when(repositorioColecciones.findById(id)).thenReturn(Optional.of(coleccion));
    Hecho hecho1 = mock(Hecho.class);
    Hecho hecho2 = mock(Hecho.class);
    List<Hecho> hechosEsperados = List.of(hecho1, hecho2);
    when(repositorioHechos.filtrarPorCriterios(anyList(), eq(coleccion.getConsenso())))
            .thenReturn(hechosEsperados);

    List<Hecho> resultado = service.getHechosFiltrados(id, ModosDeNavegacion.CURADA, filtros);

    assertEquals(hechosEsperados, resultado);
    verify(repositorioColecciones).findById(id);
    verify(repositorioHechos).filtrarPorCriterios(anyList(), eq(coleccion.getConsenso()));
  }

  @Test
  void agregarFuenteDeDatos_llamaAlRepositorio() {
    when(repositorioColecciones.findById(id)).thenReturn(Optional.of(coleccion));
    service.agregarFuenteDeDatos(id, 5);

    boolean existe = coleccion.getCriterios().stream()
            .filter(c -> c instanceof CriterioFuenteDeDatos)
            .map(c -> (CriterioFuenteDeDatos) c)
            .anyMatch(c -> c.getIdFuenteDeDatos() == 5 && Boolean.TRUE.equals(c.getInclusion()));
    assertTrue(existe, "El criterio con idFuente=5 y inclusion=true debe estar presente");
    verify(repositorioColecciones).save(coleccion);
  }

  @Test
  void eliminarFuenteDeDatos_quitaFuente() {
    when(repositorioColecciones.findById(id)).thenReturn(Optional.of(coleccion));
    service.agregarFuenteDeDatos(id, 5);
    service.eliminarFuenteDeDatos(id, 5);
    // Verificamos que ningún criterio de la colección tenga idFuente = 5
    boolean existe = coleccion.getCriterios().stream()
            .filter(c -> c instanceof CriterioFuenteDeDatos)
            .map(c -> (CriterioFuenteDeDatos) c)
            .anyMatch(c -> c.getIdFuenteDeDatos() == 5);
    assertFalse(existe, "El criterio con idFuente=5 debe haber sido eliminado");

    verify(repositorioColecciones, times(2)).save(coleccion);
  }

  @Test
  void modificarAlgoritmo_lanzaExcepcion_siConsensoNoExiste() {
    Map<String, Object> body = Map.of("consenso", "Inexistente");
    when(repositorioColecciones.findById(id)).thenReturn(Optional.of(coleccion));
    when(repositorioConsenso.findByDescripcion("Inexistente")).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> service.modificarAlgoritmo(id, body));
  }
}