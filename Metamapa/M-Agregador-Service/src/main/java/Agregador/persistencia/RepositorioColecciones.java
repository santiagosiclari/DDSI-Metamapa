package Agregador.persistencia;
import Agregador.business.Agregador.Agregador;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.Hecho;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class RepositorioColecciones {
  private final ArrayList<Coleccion> colecciones = new ArrayList<>();

  // === Búsquedas básicas ===
  public Optional<Coleccion> findById(UUID uuid) {
    if (uuid == null) return Optional.empty();
    return colecciones.stream().filter(c -> uuid.equals(c.getHandle())).findFirst();
  }

  public List<Coleccion> findAll() {
    return colecciones;
  }

  // === Altas / Bajas / Modificaciones ===

  /** Agrega una colección nueva (asegura unicidad por handle). */
  public void crear(Coleccion coleccion) {
    Objects.requireNonNull(coleccion, "coleccion no puede ser null");
    // Evita duplicado por UUID
    colecciones.removeIf(c -> c.getHandle().equals(coleccion.getHandle()));
    colecciones.add(coleccion);
  }

  /** Crea y devuelve el UUID de la colección creada (útil para Services/Controllers). */
  public UUID crear(String titulo, String descripcion, Consenso consenso,
                    List<Criterio> pertenencia, List<Criterio> noPertenencia) {
    ArrayList<Criterio> p = pertenencia == null ? new ArrayList<>() : new ArrayList<>(pertenencia);
    ArrayList<Criterio> np = noPertenencia == null ? new ArrayList<>() : new ArrayList<>(noPertenencia);
    Coleccion c = new Coleccion(titulo, descripcion, consenso, p, np);
    crear(c);
    return c.getHandle();
  }

  /** Reemplaza la colección por handle (upsert por UUID). */
  public void update(Coleccion coleccion) {
    Objects.requireNonNull(coleccion, "coleccion no puede ser null");
    colecciones.removeIf(c -> c.getHandle().equals(coleccion.getHandle()));
    colecciones.add(coleccion);
  }

  /** Elimina por UUID. */
  public boolean eliminarPorId(UUID id) {
    if (id == null) return false;
    return colecciones.removeIf(c -> id.equals(c.getHandle()));
  }

  // === Cambios de configuración de la colección ===

  /** Cambia el consenso de la colección. */
  public boolean modificarConsenso(UUID id, Consenso consenso) {
    Objects.requireNonNull(id, "id no puede ser null");
    Objects.requireNonNull(consenso, "consenso no puede ser null");
    return findById(id).map(c -> { c.setConsenso(consenso); return true; }).orElse(false);
  }

  /** Cambia el modo de navegación (IRRESTRICTA / RESTRINGIDA). */
  public boolean modificarModoNavegacion(UUID id, ModosDeNavegacion modo) {
    Objects.requireNonNull(id, "id no puede ser null");
    Objects.requireNonNull(modo, "modo no puede ser null");
    return findById(id).map(c -> { c.setModoNavegacion(modo); return true; }).orElse(false);
  }

  // === Gestión de criterios (pertenencia / no pertenencia) ===

  public boolean agregarCriterioPertenencia(UUID id, Criterio criterio) {
    Objects.requireNonNull(criterio, "criterio no puede ser null");
    return findById(id).map(c -> { c.agregarCriterioPertenencia(criterio); return true; }).orElse(false);
  }

  public boolean eliminarCriterioPertenencia(UUID id, Criterio criterio) {
    Objects.requireNonNull(criterio, "criterio no puede ser null");
    return findById(id).map(c -> { c.eliminarCriterioPertenencia(criterio); return true; }).orElse(false);
  }

  public boolean agregarCriterioNoPertenencia(UUID id, Criterio criterio) {
    Objects.requireNonNull(criterio, "criterio no puede ser null");
    return findById(id).map(c -> { c.agregarCriterioNoPertenencia(criterio); return true; }).orElse(false);
  }

  public boolean eliminarCriterioNoPertenencia(UUID id, Criterio criterio) {
    Objects.requireNonNull(criterio, "criterio no puede ser null");
    return findById(id).map(c -> { c.eliminarCriterioNoPertenencia(criterio); return true; }).orElse(false);
  }

  /**
   * Reemplaza “todo” el set de criterios de pertenencia.
   * Útil para PATCH/PUT donde te llega la lista completa desde UI.
   */
  public boolean setCriteriosPertenencia(UUID id, List<Criterio> nuevos) {
    Objects.requireNonNull(nuevos, "nuevos no puede ser null");
    return findById(id).map(c -> {
      c.setCriterioPertenencia(new ArrayList<>(nuevos));
      return true;
    }).orElse(false);
  }

  /** Reemplaza “todo” el set de criterios de no pertenencia. */
  public boolean setCriteriosNoPertenencia(UUID id, List<Criterio> nuevos) {
    Objects.requireNonNull(nuevos, "nuevos no puede ser null");
    return findById(id).map(c -> {
      c.setCriterioNoPertenencia(new ArrayList<>(nuevos));
      return true;
    }).orElse(false);
  }

  // === Hechos de la colección (según modo/consenso/criterios) ===

  /**
   * Devuelve los hechos de la colección aplicando criterios y consenso
   * según el modo indicado. Usa el snapshot del Agregador.
   */
/*  public List<Hecho> obtenerHechos(UUID idColeccion, ModosDeNavegacion modo) {
    Coleccion col = buscarXUUID(idColeccion).orElse(null);
    if (col == null) return List.of();

    // Snapshot actual del agregador
    List<Hecho> hechos = Agregador.getInstance().getListaHechos();
    // El dominio de Coleccion ya sabe filtrar y curar
    return col.getHechos(new ArrayList<>(hechos), modo);
  }*/

  /**
   * Igual que obtenerHechos, pero te permite pasar criterios adicionales (ORIGINADOS en la vista/consulta).
   * Se aplican sumando a los definidos en la colección.
   */
  public List<Hecho> obtenerHechos(UUID idColeccion,
                                   List<Criterio> criteriosAdicionalP,
                                   List<Criterio> criteriosAdicionalNP,
                                   ModosDeNavegacion modo) {
    Coleccion col = findById(idColeccion).orElse(null);
    if (col == null) return List.of();

    List<Hecho> hechos = Agregador.getInstance().getListaHechos();
    return col.filtrarPorCriterios(
            new ArrayList<>(hechos),
            criteriosAdicionalP == null ? new ArrayList<>() : new ArrayList<>(criteriosAdicionalP),
            criteriosAdicionalNP == null ? new ArrayList<>() : new ArrayList<>(criteriosAdicionalNP),
            modo
    );
  }

  // === Helpers de conveniencia para Controllers/Services ===

  /** Renombra título y descripción (PUT/PATCH ligero). */
  public boolean actualizarMetadatos(UUID id, String nuevoTitulo, String nuevaDescripcion) {
    return findById(id).map(c -> {
      if (nuevoTitulo != null) c.setTitulo(nuevoTitulo);
      if (nuevaDescripcion != null) c.setDescripcion(nuevaDescripcion);
      return true;
    }).orElse(false);
  }

  // === Semilla / demo (opcional) ===
  // Descomentá si querés subir el servicio con una colección ejemplo
  /*
  public RepositorioColecciones() {
    // Ejemplo: crea una colección vacía con consenso Absoluto, sin criterios
    UUID demo = crear("Colección Demo", "Para probar endpoints",
        Consenso.ABSOLUTO,
        List.of(), List.of());
    modificarModoNavegacion(demo, ModosDeNavegacion.IRRESTRICTA);
  }
  */
}