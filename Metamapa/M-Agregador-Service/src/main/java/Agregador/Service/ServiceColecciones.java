package Agregador.Service;
import Agregador.DTO.*;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.*;
import Agregador.persistencia.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.*;

@Service
public class ServiceColecciones {
  public final RepositorioColecciones repositorioColecciones;
  public final RepositorioHechosImpl repositorioHechos;
  public final RepositorioConsenso repositorioConsenso;

  public ServiceColecciones(RepositorioColecciones repositorioColecciones, RepositorioHechosImpl repositorioHechos, RepositorioConsenso repositorioConsenso) {
    this.repositorioHechos = repositorioHechos;
    this.repositorioColecciones = repositorioColecciones;
    this.repositorioConsenso = repositorioConsenso;
  }

  public List<Hecho> getHechosFiltrados(UUID id, ModosDeNavegacion modoNavegacion, FiltrosHechosDTO filtros) {
    Coleccion coleccion = repositorioColecciones.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    System.out.println("Filtros recibidos: " + filtros);
    List<Criterio> inclusion = construirCriteriosInclusion(filtros);
    List<Criterio> exclusion = construirCriteriosExclusion(filtros);
    List<Criterio> todosLosCriterios = Stream.of(
                    coleccion.getCriterios().stream(),
                    inclusion.stream(),
                    exclusion.stream()
            )
            .flatMap(s -> s)   // achata todos los streams en uno solo
            .distinct()        // evita duplicados
            .toList();
    System.out.println("Filtros totales: " + todosLosCriterios);
    if (modoNavegacion == ModosDeNavegacion.CURADA) {
      Consenso consensoColeccion = coleccion.getConsenso();
      System.out.println("Consenso: " + consensoColeccion.toString());
      return repositorioHechos.filtrarPorCriterios(todosLosCriterios, consensoColeccion);
    }
    return repositorioHechos.filtrarPorCriterios(todosLosCriterios, null);
  }

  private List<Criterio> construirCriteriosInclusion(FiltrosHechosDTO filtros) {
    List<Criterio> inclusion = new ArrayList<>();
    if (filtros.getTituloP() != null) inclusion.add(new CriterioTitulo(filtros.getTituloP(), true));
    if (filtros.getDescripcionP() != null) inclusion.add(new CriterioDescripcion(filtros.getDescripcionP(), true));
    if (filtros.getCategoriaP() != null) inclusion.add(new CriterioCategoria(filtros.getCategoriaP(), true));
    if (filtros.getFechaReporteDesdeP() != null || filtros.getFechaReporteHastaP() != null)
      inclusion.add(new CriterioFechaReportaje(filtros.getFechaReporteDesdeP(), filtros.getFechaReporteHastaP(), true));
    if (filtros.getFechaAcontecimientoDesdeP() != null || filtros.getFechaAcontecimientoHastaP() != null)
      inclusion.add(new CriterioFecha(filtros.getFechaAcontecimientoDesdeP(), filtros.getFechaAcontecimientoHastaP(), true));
    if (filtros.getLatitudP() != null && filtros.getLongitudP() != null)
      inclusion.add(new CriterioUbicacion(filtros.getLatitudP(), filtros.getLongitudP(), true));
    if (filtros.getTipoMultimediaP() != null)
      inclusion.add(new CriterioMultimedia(TipoMultimedia.valueOf(filtros.getTipoMultimediaP()), true));
    return inclusion;
  }

  private List<Criterio> construirCriteriosExclusion(FiltrosHechosDTO filtros) {
    List<Criterio> exclusion = new ArrayList<>();
    if (filtros.getTituloNP() != null) exclusion.add(new CriterioTitulo(filtros.getTituloNP(), false));
    if (filtros.getDescripcionNP() != null) exclusion.add(new CriterioDescripcion(filtros.getDescripcionNP(), false));
    if (filtros.getCategoriaNP() != null) exclusion.add(new CriterioCategoria(filtros.getCategoriaNP(), false));
    if (filtros.getFechaReporteDesdeNP() != null || filtros.getFechaReporteHastaNP() != null)
      exclusion.add(new CriterioFechaReportaje(filtros.getFechaReporteDesdeNP(), filtros.getFechaReporteHastaNP(), false));
    if (filtros.getFechaAcontecimientoDesdeNP() != null || filtros.getFechaAcontecimientoHastaNP() != null)
      exclusion.add(new CriterioFecha(filtros.getFechaAcontecimientoDesdeNP(), filtros.getFechaAcontecimientoHastaNP(), false));
    if (filtros.getLatitudNP() != null && filtros.getLongitudNP() != null)
      exclusion.add(new CriterioUbicacion(filtros.getLatitudNP(), filtros.getLongitudNP(), false));
    if (filtros.getTipoMultimediaNP() != null)
      exclusion.add(new CriterioMultimedia(TipoMultimedia.valueOf(filtros.getTipoMultimediaNP()), false));
    return exclusion;
  }

  public List<ColeccionDTO> obtenerTodasLasColecciones() {
    return repositorioColecciones.findAll().stream()
            .map(ColeccionDTO::new) // convierte cada Coleccion a DTO
            .collect(Collectors.toList());
  }

  public ColeccionDTO obtenerColeccionPorId(UUID id) {
    Coleccion coleccion = repositorioColecciones.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    return new ColeccionDTO(coleccion);
  }

  public ColeccionDTO crearColeccion(ColeccionDTO coleccionDTO) {
    String nombre = coleccionDTO.getConsenso();
    // Buscar el consenso existente
    Consenso consenso = repositorioConsenso.findByDescripcion(nombre)
            .orElseThrow(() -> new RuntimeException("Consenso no encontrado: " + nombre));
    ArrayList<Criterio> criterios = coleccionDTO.getCriterios().stream()
            .map(CriterioDTO::toDomain)
            .collect(Collectors.toCollection(ArrayList::new));
    Coleccion coleccion = new Coleccion(coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), consenso, criterios);
    repositorioColecciones.save(coleccion);
    return new ColeccionDTO(coleccion);
  }

  public ColeccionDTO actualizarColeccion(UUID id, ColeccionDTO coleccionDTO) {
    Coleccion coleccion = repositorioColecciones.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    if (coleccionDTO.getTitulo() != null) coleccion.setTitulo(coleccionDTO.getTitulo());
    if (coleccionDTO.getDescripcion() != null) coleccion.setDescripcion(coleccionDTO.getDescripcion());
    if (coleccionDTO.getConsenso() != null) coleccion.setConsenso(Consenso.fromString(coleccionDTO.getConsenso()));
    if (coleccionDTO.getCriterios() != null) {
      List<Criterio> nuevosCriterios = coleccionDTO.getCriterios().stream()
              .map(CriterioDTO::toDomain)
              .toList();
      coleccion.setCriterios(new ArrayList<>(nuevosCriterios));
    }
    return new ColeccionDTO(coleccion);
  }

  public void modificarAlgoritmo(UUID id, Map<String, Object> body) {
    Coleccion c = repositorioColecciones.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada"));
    String nombre = null;
    if (body.get("consenso") != null) nombre = body.get("consenso").toString();
    if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El campo 'consenso' es obligatorio");
    c.setConsenso(Consenso.fromString(nombre.trim()));
    repositorioColecciones.save(c);
  }

  public void eliminarColeccion(UUID id) {
    repositorioColecciones.deleteById(id);
  }

  public void agregarFuenteDeDatos(UUID idColeccion, Integer idFuente) {
    Coleccion col = repositorioColecciones.findById(idColeccion)
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada"));
    col.agregarCriterio(new CriterioFuenteDeDatos(idFuente, true));
    repositorioColecciones.save(col);
  }

  public void eliminarFuenteDeDatos(UUID idColeccion, Integer idFuente) {
    Coleccion col = repositorioColecciones.findById(idColeccion)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    col.eliminarCriterio(new CriterioFuenteDeDatos(idFuente, true));
    repositorioColecciones.save(col);
  }
}