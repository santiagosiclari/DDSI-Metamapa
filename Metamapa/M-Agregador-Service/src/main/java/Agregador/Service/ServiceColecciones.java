package Agregador.Service;
import Agregador.DTO.ColeccionDTO;
import Agregador.DTO.CriterioDTO;
import Agregador.DTO.FiltrosHechosDTO;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.*;
import Agregador.persistencia.RepositorioColecciones;
import Agregador.persistencia.RepositorioHechos;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.*;

@Service
public class ServiceColecciones {
  public final RepositorioColecciones repositorioColecciones;
  public final RepositorioHechos repositorioHechos;

  public ServiceColecciones(RepositorioColecciones repositorioColecciones, RepositorioHechos repositorioHechos) {
    this.repositorioHechos = repositorioHechos;
    this.repositorioColecciones = repositorioColecciones;
  }

  public List<Hecho> getHechosFiltrados(UUID id, ModosDeNavegacion modoNavegacion, FiltrosHechosDTO filtros) {
    //System.out.println("Filtros recibidos: " + filtros);
    Coleccion coleccion = repositorioColecciones.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    List<Criterio> inclusion = construirCriteriosInclusion(filtros);
    List<Criterio> exclusion = construirCriteriosExclusion(filtros);
    List<Criterio> totalFiltrosInclusion = Stream.concat(coleccion.getCriterioPertenencia().stream(), inclusion.stream())
            .distinct()
            .collect(Collectors.toList());
    List<Criterio> totalFiltrosExclusion = Stream.concat(coleccion.getCriterioNoPertenencia().stream(), exclusion.stream())
            .distinct()
            .collect(Collectors.toList());
    //System.out.println("Filtros totales: " + totalFiltrosInclusion);
    //System.out.println("Filtros totales: " + totalFiltrosExclusion);
    // if (modoNavegacion == ModosDeNavegacion.IRRESTRICTA)
    return repositorioHechos.filtrarPorCriterios(totalFiltrosInclusion,totalFiltrosExclusion);
    //TODO implementar modo CURADA
  }

  private List<Criterio> construirCriteriosInclusion(FiltrosHechosDTO filtros) {
    List<Criterio> inclusion = new ArrayList<>();
    if (filtros.getTituloP() != null) inclusion.add(new CriterioTitulo(filtros.getTituloP()));
    if (filtros.getDescripcionP() != null) inclusion.add(new CriterioDescripcion(filtros.getDescripcionP()));
    if (filtros.getCategoriaP() != null) inclusion.add(new CriterioCategoria(filtros.getCategoriaP()));
    if (filtros.getFechaReporteDesdeP() != null || filtros.getFechaReporteHastaP() != null)
      inclusion.add(new CriterioFechaReportaje(filtros.getFechaReporteDesdeP(), filtros.getFechaReporteHastaP()));
    if (filtros.getFechaAcontecimientoDesdeP() != null || filtros.getFechaAcontecimientoHastaP() != null)
      inclusion.add(new CriterioFecha(filtros.getFechaAcontecimientoDesdeP(), filtros.getFechaAcontecimientoHastaP()));
    if (filtros.getLatitudP() != null && filtros.getLongitudP() != null)
      inclusion.add(new CriterioUbicacion(filtros.getLatitudP(), filtros.getLongitudP()));
    if (filtros.getTipoMultimediaP() != null)
      inclusion.add(new CriterioMultimedia(TipoMultimedia.valueOf(filtros.getTipoMultimediaP())));
    return inclusion;
  }

  private List<Criterio> construirCriteriosExclusion(FiltrosHechosDTO filtros) {
    List<Criterio> exclusion = new ArrayList<>();
    if (filtros.getTituloNP() != null) exclusion.add(new CriterioTitulo(filtros.getTituloNP()));
    if (filtros.getDescripcionNP() != null) exclusion.add(new CriterioDescripcion(filtros.getDescripcionNP()));
    if (filtros.getCategoriaNP() != null) exclusion.add(new CriterioCategoria(filtros.getCategoriaNP()));
    if (filtros.getFechaReporteDesdeNP() != null || filtros.getFechaReporteHastaNP() != null)
      exclusion.add(new CriterioFechaReportaje(filtros.getFechaReporteDesdeNP(), filtros.getFechaReporteHastaNP()));
    if (filtros.getFechaAcontecimientoDesdeNP() != null || filtros.getFechaAcontecimientoHastaNP() != null)
      exclusion.add(new CriterioFecha(filtros.getFechaAcontecimientoDesdeNP(), filtros.getFechaAcontecimientoHastaNP()));
    if (filtros.getLatitudNP() != null && filtros.getLongitudNP() != null)
      exclusion.add(new CriterioUbicacion(filtros.getLatitudNP(), filtros.getLongitudNP()));
    if (filtros.getTipoMultimediaNP() != null)
      exclusion.add(new CriterioMultimedia(TipoMultimedia.valueOf(filtros.getTipoMultimediaNP())));
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
    String nombre = coleccionDTO.getConsenso(); // o de tu body Map: (String) body.get("consenso")
    Consenso consenso = Consenso.fromString(nombre);

    ArrayList<Criterio> pertenencia = coleccionDTO.getCriteriosPertenencia().stream()
            .map(CriterioDTO::toDomain)
            .collect(Collectors.toCollection(ArrayList::new));
    ArrayList<Criterio> noPertenencia = coleccionDTO.getCriteriosNoPertenencia().stream()
            .map(CriterioDTO::toDomain)
            .collect(Collectors.toCollection(ArrayList::new));

    Coleccion coleccion = new Coleccion(coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), consenso, pertenencia, noPertenencia);

    repositorioColecciones.crear(coleccion);
    return new ColeccionDTO(coleccion);
  }

  public ColeccionDTO actualizarColeccion(UUID id, ColeccionDTO coleccionDTO) {
    Coleccion coleccion = repositorioColecciones.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    if (coleccionDTO.getTitulo() != null) coleccion.setTitulo(coleccionDTO.getTitulo());
    if (coleccionDTO.getDescripcion() != null) coleccion.setDescripcion(coleccionDTO.getDescripcion());
    if (coleccionDTO.getConsenso() != null) coleccion.setConsenso(Consenso.fromString(coleccionDTO.getConsenso()));
    if (coleccionDTO.getCriteriosPertenencia() != null) {
      List<Criterio> nuevosCriterios = coleccionDTO.getCriteriosPertenencia().stream()
              .map(CriterioDTO::toDomain)
              .toList();
      coleccion.setCriterioPertenencia(new ArrayList<>(nuevosCriterios));
    }
    if (coleccionDTO.getCriteriosNoPertenencia() != null) {
      List<Criterio> nuevosCriterios = coleccionDTO.getCriteriosNoPertenencia().stream()
              .map(CriterioDTO::toDomain)
              .toList();
      coleccion.setCriterioNoPertenencia(new ArrayList<>(nuevosCriterios));
    }
    return new ColeccionDTO(coleccion);
  }

  public void modificarAlgoritmo(UUID id, Map<String, Object> body) {
    Coleccion c = repositorioColecciones.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada"));

    String nombre = null;
    if (body.get("consenso") != null) nombre = body.get("consenso").toString();

    if (nombre == null || nombre.isBlank())
      throw new IllegalArgumentException("El campo 'consenso' es obligatorio");

    c.setConsenso(Consenso.fromString(nombre.trim()));
    repositorioColecciones.update(c);
  }

  public boolean eliminarColeccion(UUID id) {
    return repositorioColecciones.eliminarPorId(id);
  }

  public void agregarFuenteDeDatos(UUID idColeccion, Integer idFuente) {
    Coleccion col = repositorioColecciones.findById(idColeccion)
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada"));
    col.agregarCriterioPertenencia(new CriterioFuenteDeDatos(idFuente));
    repositorioColecciones.update(col);
  }

  public void eliminarFuenteDeDatos(UUID idColeccion, Integer idFuente) {
    Coleccion col = repositorioColecciones.findById(idColeccion)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    col.eliminarCriterioPertenencia(new CriterioFuenteDeDatos(idFuente));
    repositorioColecciones.update(col);
  }
}