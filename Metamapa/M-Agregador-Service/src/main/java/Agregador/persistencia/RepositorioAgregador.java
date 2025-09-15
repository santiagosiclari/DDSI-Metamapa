package Agregador.persistencia;
//
//import java.Agregador.business.Agregador.Agregador;
import Agregador.business.Agregador.Agregador;
import Agregador.business.Hechos.Hecho;
import Agregador.business.Solicitudes.SolicitudEliminacion;
import Agregador.business.Solicitudes.SolicitudEdicion;
// (Opcional) si usás estados
// import java.Solicitudes.business.Agregador.EstadoSolicitud;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repositorio en memoria para el Agregador (Entrega 4):
 * - Mantiene el singleton Agregador (lista de hechos agregados).
 * - Mantiene Fuentes activas (por id) —estado local.
 * - Persiste solicitudes (eliminación / edición) en estructuras en memoria.
 *
 * NOTA: La lógica de "actualizar hechos desde fuentes externas" se hace en Service.
 *       El Service consulta fuentes (REST/CSV/etc.), arma List<Hecho> y luego llama a
 *       repositorio.reemplazarHechos(hechosActualizados).
 */
@Repository
public class RepositorioAgregador {

  // === Núcleo del dominio ===
  @Getter
  private final Agregador agregador;

  // === Estado propio del repositorio ===
  // Fuentes activas (si tu dominio maneja objetos fuente).
  private final Map<Integer, Object> fuentesActivas = new ConcurrentHashMap<>();
  // Si tuvieras una superclase/interfaz FuenteDeDatos, reemplazá Object por esa clase.

  // Solicitudes (persistencia en memoria)
  private final Map<Integer, SolicitudEliminacion> solicitudesEliminacion = new ConcurrentHashMap<>();
  private final Map<Integer, SolicitudEdicion> solicitudesEdicion = new ConcurrentHashMap<>();
  private final AtomicInteger secuenciaSolicitudesEliminacion = new AtomicInteger(1);
  private final AtomicInteger secuenciaSolicitudesEdicion = new AtomicInteger(1);

  // Toggle para cargar datos demo (puede venir de application.yml)
  private final boolean cargarDatosDemo = true;

  public RepositorioAgregador() {
    this.agregador = Agregador.getInstance();

    // Semillado opcional (útil para probar endpoints rápidamente)
    if (cargarDatosDemo) {
      seedDemo();
    }
  }

  // ============================================================
  //                    HECHOS AGREGADOS
  // ============================================================

  /**
   * Retorna la lista actual de hechos agregados (ya normalizados).
   * IMPORTANTE: El Agregador tiene @Getter ArrayList<Hecho> listaHechos;
   *             por lo tanto, el getter es getListaHechos() (sin "De").
   */
public List<Hecho> getHechos() {
    return agregador.getListaHechos();
}
 /**
  * Reemplaza todos los hechos agregados por la lista provista (típico “refresh”
  * luego de consultar fuentes externas desde el Service).
  */
 public void reemplazarHechos(Collection<Hecho> nuevosHechos) {
   ArrayList<Hecho> destino = agregador.getListaHechos();
   destino.clear();
   if (nuevosHechos != null && !nuevosHechos.isEmpty()) {
     destino.addAll(nuevosHechos);
   }
 }
 /**
  * Agrega hechos “incrementalmente” (si tu estrategia no quiere reemplazar todo).
  */
 public void agregarHechos(Collection<Hecho> hechos) {
   if (hechos == null || hechos.isEmpty()) return;
   agregador.getListaHechos().addAll(hechos);
 }
 /**
  * Vacía los hechos agregados (útil para pruebas / reset).
  */
 public void limpiarHechos() {
   agregador.getListaHechos().clear();
 }
 // ============================================================
 //                        FUENTES
 // ============================================================
 /**
  * Registra una fuente activa en el repositorio (en memoria).
  * Suele llamarse desde el Service cuando el usuario “agrega” una fuente.
  */
 public void registrarFuenteActiva(Integer idFuente, Object fuente) {
   if (idFuente == null || fuente == null) return;
   fuentesActivas.put(idFuente, fuente);
 }
 /**
  * Remueve la fuente activa por id (solo del repositorio).
  * La baja lógica/efecto en hechos debe realizarla el Service (y luego refrescar hechos).
  */
 public void removerFuenteActiva(Integer idFuente) {
   if (idFuente == null) return;
   fuentesActivas.remove(idFuente);
 }
 /**
  * Lista (shallow) de ids de fuentes activas.
  */
 public Set<Integer> getIdsFuentesActivas() {
   return Collections.unmodifiableSet(fuentesActivas.keySet());
 }
 /**
  * Obtiene (si la guardaste) la instancia de fuente activa por id.
  * Útil si el Service necesita consultar capacidades locales de esa fuente
  * (por ejemplo, si es una FuenteEstatica in-process).
  */
 public Optional<Object> getFuenteActiva(Integer idFuente) {
   return Optional.ofNullable(fuentesActivas.get(idFuente));
 }
 /**
  * Limpia todas las fuentes activas (sólo estado local).
  */
 public void limpiarFuentesActivas() {
   fuentesActivas.clear();
 }
 // ============================================================
 //                       SOLICITUDES
 // ============================================================
 // ==== Eliminación ====
 public Optional<SolicitudEliminacion> findSolicitudEliminacionById(Integer id) {
   if (id == null) return Optional.empty();
   return Optional.ofNullable(solicitudesEliminacion.get(id));
 }
 public Integer saveSolicitudEliminacion(SolicitudEliminacion s) {
   int id = secuenciaSolicitudesEliminacion.getAndIncrement();
   // Si tu clase tiene setId(Integer), podés persistirlo ahí.
   // s.setId(id);
   solicitudesEliminacion.put(id, s);
   return id;
 }
 public List<SolicitudEliminacion> listarSolicitudesEliminacion() {
   return new ArrayList<>(solicitudesEliminacion.values());
 }
 public void eliminarSolicitudEliminacion(Integer id) {
   if (id != null) solicitudesEliminacion.remove(id);
 }
 // // Si manejás estados:
 // public void cambiarEstadoSolicitudEliminacion(Integer id, EstadoSolicitud nuevo) {
 //   SolicitudEliminacion s = solicitudesEliminacion.get(id);
 //   if (s != null) s.setEstado(nuevo);
 // }
 // ==== Edición (análogamente) ====
 public Optional<SolicitudEdicion> findSolicitudEdicionById(Integer id) {
   if (id == null) return Optional.empty();
   return Optional.ofNullable(solicitudesEdicion.get(id));
 }
 public Integer saveSolicitudEdicion(SolicitudEdicion s) {
   int id = secuenciaSolicitudesEdicion.getAndIncrement();
   // s.setId(id);
   solicitudesEdicion.put(id, s);
   return id;
 }
 public List<SolicitudEdicion> listarSolicitudesEdicion() {
   return new ArrayList<>(solicitudesEdicion.values());
 }
 public void eliminarSolicitudEdicion(Integer id) {
   if (id != null) solicitudesEdicion.remove(id);
 }
 // ============================================================
 //                  SEMILLA / DATOS DE EJEMPLO
 // ============================================================
 private void seedDemo() {
   try {
     // ===== Ejemplo 1: Fuente Dinámica in-memory =====
     // NOTA: reemplazá "Object" por tu clase FuenteDinamica si la tenés en este módulo.
     Object fuenteDinamica = crearFuenteDinamicaDemo();
     registrarFuenteActiva(2000001, fuenteDinamica);
     // ===== Ejemplo 2: Fuente Estática (CSV) =====
     Object fuenteEstatica = crearFuenteEstaticaDesdeCSV(
             "agregador-service/src/main/resources/desastres_naturales_argentina.csv"
             // Alternativa si corrés desde root del proyecto:
             // "Metamapa/agregador-service/src/main/resources/desastres_naturales_argentina.csv"
     );
     if (fuenteEstatica != null) {
       registrarFuenteActiva(2000002, fuenteEstatica);
     }
     // ===== Cargar algunos Hechos iniciales (para ver algo en /hechos) =====
     List<Hecho> ejemplos = crearHechosBasicosDemo();
     agregarHechos(ejemplos);
   } catch (Exception e) {
     // Evitamos que un error de demo rompa el arranque del servicio
     System.err.println("[RepositorioAgregador] seedDemo() falló: " + e.getMessage());
   }
 }
 // ----------------- Helpers de demo -----------------
 private Object crearFuenteDinamicaDemo() {
   // Si tu clase está disponible, creala y cargale un Hecho mínimo:
   // FuenteDinamica f = new FuenteDinamica();
   // Perfil admin01 = new Perfil("Juan", "Perez", 30);
   // Usuario admin = new Usuario("admin1@frba.utn.edu.ar", "algo", admin01,
   //    List.of(Rol.ADMINISTRADOR, Rol.CONTRIBUYENTE, Rol.VISUALIZADOR));
   //
   // f.agregarHecho(
   //     "Hecho demo",
   //     "Esto es una descripcion demo",
   //     "Metamapa/demo",
   //     0f, 0f,
   //     LocalDate.of(2025, 6, 22),
   //     admin01,
   //     false,
   //     new ArrayList<Multimedia>()
   // );
   // return f;
   return new Object(); // Placeholder si en este módulo no tenés las clases de fuente
 }
 private Object crearFuenteEstaticaDesdeCSV(String rutaCSV) {
   // Si tenés CSVHechoParser/FuenteEstatica en este módulo, cargalos aquí:
   // CSVHechoParser parser = new CSVHechoParser();
   // FuenteEstatica f = new FuenteEstatica(rutaCSV, parser);
   // f.cargarCSV(rutaCSV);
   // return f;
   return null; // si no está disponible en este módulo
 }
 private ArrayList<Hecho> crearHechosBasicosDemo() {
   // Creá 2–3 hechos mínimos para probar el endpoint GET /hechos
   List<Hecho> lista = new ArrayList<>();
   // Ejemplo genérico (ajustá al constructor de tu Hecho):
   // lista.add(new Hecho("Incendio", "Foco ígneo controlado", "demo/fuente", -34.6f, -58.4f,
   //         LocalDate.now().minusDays(2), /* autor */ null, /* verificado */ false, /* multimedia */ List.of()));
   // lista.add(new Hecho("Inundación", "Crecida de arroyo", "demo/fuente", -31.6f, -60.7f,
   //         LocalDate.now().minusDays(1), null, false, List.of()));
   return lista;
 }
}

