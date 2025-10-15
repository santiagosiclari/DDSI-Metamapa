package Agregador.Service;
import Agregador.DTO.*;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.*;
import Agregador.persistencia.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class ServiceColecciones {
  private final RepositorioColecciones repositorioColecciones;
  private final RepositorioHechosImpl repositorioHechos;
  private final RepositorioConsenso repositorioConsenso;

  public List<Hecho> getHechosFiltrados(UUID id, ModosDeNavegacion modoNavegacion, FiltrosHechosDTO filtros) {
    Coleccion coleccion = repositorioColecciones.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    System.out.println("Filtros recibidos: " + filtros);
    List<Criterio> inclusion = construirCriterios(filtros, true);
    List<Criterio> exclusion = construirCriterios(filtros, false);
    List<Criterio> todosLosCriterios = Stream
            .of(coleccion.getCriterios(), inclusion, exclusion)
            .flatMap(Collection::stream)
            .distinct()
            .toList();
    System.out.println("Filtros totales: " + todosLosCriterios);
    if (modoNavegacion == ModosDeNavegacion.CURADA) {
      Consenso consensoColeccion = coleccion.getConsenso();
      System.out.println("Consenso: " + consensoColeccion.toString());
      return repositorioHechos.filtrarPorCriterios(todosLosCriterios, consensoColeccion);
    }
    return repositorioHechos.filtrarPorCriterios(todosLosCriterios, null);
  }

  private List<Criterio> construirCriterios(FiltrosHechosDTO filtros, boolean incluir) {
    List<Criterio> criterios = new ArrayList<>();
    if (incluir) {
      if (filtros.getTituloP() != null) criterios.add(new CriterioTitulo(filtros.getTituloP(), true));
      if (filtros.getDescripcionP() != null) criterios.add(new CriterioDescripcion(filtros.getDescripcionP(), true));
      if (filtros.getCategoriaP() != null) criterios.add(new CriterioCategoria(filtros.getCategoriaP(), true));
      if (filtros.getFechaReporteDesdeP() != null || filtros.getFechaReporteHastaP() != null)
        criterios.add(new CriterioFechaReportaje(filtros.getFechaReporteDesdeP(), filtros.getFechaReporteHastaP(), true));
      if (filtros.getFechaAcontecimientoDesdeP() != null || filtros.getFechaAcontecimientoHastaP() != null)
        criterios.add(new CriterioFecha(filtros.getFechaAcontecimientoDesdeP(), filtros.getFechaAcontecimientoHastaP(), true));
      if (filtros.getLatitudP() != null && filtros.getLongitudP() != null)
        criterios.add(new CriterioUbicacion(filtros.getLatitudP(), filtros.getLongitudP(),filtros.getRadio(), true));
      if (filtros.getTipoMultimediaP() != null) criterios.add(new CriterioMultimedia(TipoMultimedia.valueOf(filtros.getTipoMultimediaP()), true));
    } else {
      if (filtros.getTituloNP() != null) criterios.add(new CriterioTitulo(filtros.getTituloNP(), false));
      if (filtros.getDescripcionNP() != null) criterios.add(new CriterioDescripcion(filtros.getDescripcionNP(), false));
      if (filtros.getCategoriaNP() != null) criterios.add(new CriterioCategoria(filtros.getCategoriaNP(), false));
      if (filtros.getFechaReporteDesdeNP() != null || filtros.getFechaReporteHastaNP() != null)
        criterios.add(new CriterioFechaReportaje(filtros.getFechaReporteDesdeNP(), filtros.getFechaReporteHastaNP(), false));
      if (filtros.getFechaAcontecimientoDesdeNP() != null || filtros.getFechaAcontecimientoHastaNP() != null)
        criterios.add(new CriterioFecha(filtros.getFechaAcontecimientoDesdeNP(), filtros.getFechaAcontecimientoHastaNP(), false));
      if (filtros.getLatitudNP() != null && filtros.getLongitudNP() != null)
        criterios.add(new CriterioUbicacion(filtros.getLatitudNP(), filtros.getLongitudNP(), false));
      if (filtros.getTipoMultimediaNP() != null) criterios.add(new CriterioMultimedia(TipoMultimedia.valueOf(filtros.getTipoMultimediaNP()), false));
    }
    return criterios;
  }

  public List<ColeccionDTO> obtenerTodasLasColecciones() {
    return repositorioColecciones.findAll().stream()
            .map(ColeccionDTO::new) // convierte cada Coleccion a DTO
            .collect(Collectors.toList());
  }

  public ColeccionDTO obtenerColeccionPorId(UUID id) {
    Coleccion coleccion = repositorioColecciones.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada con ID: " + id));
    return new ColeccionDTO(coleccion);
  }

  public ColeccionDTO crearColeccion(ColeccionDTO coleccionDTO) {
    String nombre = coleccionDTO.getConsenso();
    // Buscar el consenso existente
    Consenso consenso = repositorioConsenso.findByDescripcion(nombre)
            .orElseThrow(() -> new NoSuchElementException("Consenso no encontrado: " + nombre));
    ArrayList<Criterio> criterios = coleccionDTO.getCriterios().stream()
            .map(CriterioDTO::toDomain)
            .collect(Collectors.toCollection(ArrayList::new));
    Coleccion coleccion = new Coleccion(coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), consenso, criterios);
    repositorioColecciones.save(coleccion);
    return new ColeccionDTO(coleccion);
  }

  public ColeccionDTO actualizarColeccion(UUID id, ColeccionDTO dto) {
    Coleccion coleccion = repositorioColecciones.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada con ID: " + id));
    coleccion.setTitulo(dto.getTitulo());
    coleccion.setDescripcion(dto.getDescripcion());
    String nombre = dto.getConsenso();
    Consenso consenso = repositorioConsenso.findByDescripcion(nombre)
            .orElseThrow(() -> new NoSuchElementException("Consenso no encontrado: " + nombre));
    coleccion.setConsenso(consenso);
    List<Criterio> criterios = dto.getCriterios() == null ? List.of() :
            dto.getCriterios().stream()
                    .map(CriterioDTO::toDomain)
                    .toList();
    coleccion.getCriterios().clear();
    coleccion.getCriterios().addAll(criterios);
    repositorioColecciones.save(coleccion);
    return new ColeccionDTO(coleccion);
  }

  public void modificarAlgoritmo(UUID id, Map<String, Object> body) {
    Coleccion c = repositorioColecciones.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada"));
    String nombre = Optional.ofNullable(body.get("consenso"))
            .map(Object::toString)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .orElseThrow(() -> new IllegalArgumentException("El campo 'consenso' es obligatorio"));
    Consenso consenso = repositorioConsenso.findByDescripcion(nombre)
            .orElseThrow(() -> new NoSuchElementException("Consenso no encontrado: " + nombre));
    c.setConsenso(consenso);
    repositorioColecciones.save(c);
  }

  public void eliminarColeccion(UUID id) {
    if (!repositorioColecciones.existsById(id))
      throw new IllegalArgumentException("Colección no encontrada con ID: " + id);
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
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada"));
    col.eliminarCriterio(new CriterioFuenteDeDatos(idFuente, true));
    repositorioColecciones.save(col);
  }
}