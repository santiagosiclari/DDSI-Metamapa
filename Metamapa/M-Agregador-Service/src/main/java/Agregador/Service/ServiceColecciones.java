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
    Coleccion coleccion = repositorioColecciones.getColeccion(id)
        .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada: " + id));
    List<Criterio> criterios = new ArrayList<>(coleccion.getCriterios());
    criterios.addAll(repositorioHechos.construirCriterios(filtros, true));
    criterios.addAll(repositorioHechos.construirCriterios(filtros, false));

    Consenso consenso = null;
    if (modoNavegacion == ModosDeNavegacion.CURADA && coleccion.getConsenso() != null) {
      consenso = repositorioConsenso.findByNombreTipo(coleccion.getConsenso().getNombreTipo())
          .orElse(null);
    }
    List<Hecho> hechos = repositorioHechos.filtrarPorCriterios(criterios, consenso);
    return hechos;
  }
  public List<Coleccion> getColecciones() {
    return repositorioColecciones.findAll();
  }
  public Optional<Coleccion> getColeccion(UUID id) {
    return repositorioColecciones.getColeccion(id);
  }

  public List<Coleccion> getColecciones(String query) {
    System.out.println("Buscando colecciones por texto libre: " + query);
    if (query == null || query.trim().isEmpty()) {
      return repositorioColecciones.findAll();
    }
    return repositorioColecciones.buscarTextoLibre(query);
  }

  public ColeccionDTO crearColeccion(ColeccionDTO coleccionDTO) {
    String nombre = coleccionDTO.getConsenso();
    Consenso consenso = repositorioConsenso.findByNombreTipo(nombre)
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
    Consenso consenso = repositorioConsenso.findByNombreTipo(nombre)
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
    Consenso consenso = repositorioConsenso.findByNombreTipo(nombre)
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
    col.getCriterios().removeIf(c ->
        c instanceof CriterioFuenteDeDatos cfd &&
            cfd.getIdFuenteDeDatos().equals(idFuente)
    );
    repositorioColecciones.save(col);
  }
}
