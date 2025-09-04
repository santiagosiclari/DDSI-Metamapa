package Agregador.Service;

import Agregador.business.Hechos.Hecho;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Normaliza y unifica hechos antes de persistir:
 * - Mapea categorías a un canon (sinónimos -> canonical)
 * - Limpia texto (minúsculas, sin tildes, espacios)
 * - Redondea lat/lon a N decimales
 * - Deduplica por (titulo_norm, fechaHecho, lat_norm, lon_norm) y MERGEA campos
 */
@Component
public class Normalizador {

  private final Map<String, String> categoriasCanon; // clave: variante normalizada, valor: categoría canónica
  private final int decimalesCoordenadas;

  public Normalizador(@Value("${normalizador.decimales:4}") int decimalesCoordenadas) {
    this.categoriasCanon = defaultDict(); // o cargar por otro lado
    this.decimalesCoordenadas = decimalesCoordenadas;
  }

  /** === API principal === */
  public List<Hecho> normalizarYUnificar(List<Hecho> hechos) {
    if (hechos == null || hechos.isEmpty()) return List.of();

    // 1) normalizar campos
    List<Hecho> normalizados = hechos.stream()
            .map(this::normalizarHecho)
            .collect(Collectors.toList());

    // 2) deduplicar + merge según clave compuesta
    Map<String, Hecho> porClave = new LinkedHashMap<>();
    for (Hecho h : normalizados) {
      String key = clave(h);
      porClave.merge(key, h, this::mergeHechos);
    }
    return new ArrayList<>(porClave.values());
  }

  /** Normaliza un hecho in-place y lo devuelve */
  public Hecho normalizarHecho(Hecho h) {
    if (h == null) return null;

    // Título / Descripción: limpiar
    if (h.getTitulo() != null)        h.setTitulo(limpiarEspacios(h.getTitulo()).trim());
    if (h.getDescripcion() != null)   h.setDescripcion(limpiarEspacios(h.getDescripcion()).trim());

    // Categoría: normalizar con diccionario
    h.setCategoria(normalizarCategoria(h.getCategoria()));

    // Lat/Lon: redondeo
    if (h.getLatitud() != null)  h.setLatitud(redondear(h.getLatitud(), decimalesCoordenadas));
    if (h.getLongitud() != null) h.setLongitud(redondear(h.getLongitud(), decimalesCoordenadas));

    // Fechas: asegurar consistencia básica (si viene nula, no forzar)
    if (h.getFechaCarga() == null)        h.setFechaCarga(LocalDate.now());
    if (h.getFechaModificacion() == null) h.setFechaModificacion(LocalDate.now());

    // Metadata: normalizar claves a lower (opcional)
    if (h.getMetadata() != null && !h.getMetadata().isEmpty()) {
      HashMap<String, String> nueva = new HashMap<>();
      h.getMetadata().forEach((k, v) -> {
        if (k != null) nueva.put(k.trim().toLowerCase(Locale.ROOT), v);
      });
      h.setMetadata(nueva);
    }

    return h;
  }

  /** === Categorías === */
  public String normalizarCategoria(String categoriaCruda) {
    if (categoriaCruda == null || categoriaCruda.isBlank()) return "desconocido";
    String clave = toComparable(categoriaCruda); // minúsculas, sin tildes, trim
    return categoriasCanon.getOrDefault(clave, "desconocido");
  }

  /** === Dedupe key === */
  private String clave(Hecho h) {
    String tituloCmp = toComparable(Optional.ofNullable(h.getTitulo()).orElse(""));
    String fecha     = Optional.ofNullable(h.getFechaHecho()).map(LocalDate::toString).orElse("0000-00-00");
    String lat       = h.getLatitud()  == null ? "null" : String.format(Locale.ROOT, "%." + decimalesCoordenadas + "f", h.getLatitud());
    String lon       = h.getLongitud() == null ? "null" : String.format(Locale.ROOT, "%." + decimalesCoordenadas + "f", h.getLongitud());
    return String.join("|", tituloCmp, fecha, lat, lon);
  }

  /** === Merge de dos hechos duplicados === */
  private Hecho mergeHechos(Hecho base, Hecho nuevo) {
    // Preferir valores NO nulos; combinar colecciones; conservar fechas coherentes
    if (isNullOrBlank(base.getDescripcion()) && !isNullOrBlank(nuevo.getDescripcion()))
      base.setDescripcion(nuevo.getDescripcion());

    if ("desconocido".equalsIgnoreCase(Optional.ofNullable(base.getCategoria()).orElse("desconocido"))
            && !isNullOrBlank(nuevo.getCategoria()))
      base.setCategoria(nuevo.getCategoria());

    if (base.getLatitud() == null && nuevo.getLatitud() != null)   base.setLatitud(nuevo.getLatitud());
    if (base.getLongitud() == null && nuevo.getLongitud() != null) base.setLongitud(nuevo.getLongitud());

    if (base.getFechaHecho() == null && nuevo.getFechaHecho() != null)
      base.setFechaHecho(nuevo.getFechaHecho());

    // anonimo / eliminado: si alguno marca true, preservar true (conservador)
    base.setAnonimo(Boolean.TRUE.equals(base.getAnonimo()) || Boolean.TRUE.equals(nuevo.getAnonimo()));
    base.setEliminado(Boolean.TRUE.equals(base.getEliminado()) || Boolean.TRUE.equals(nuevo.getEliminado()));

    // fechas de auditoría
    if (nuevo.getFechaCarga() != null && (base.getFechaCarga() == null || nuevo.getFechaCarga().isBefore(base.getFechaCarga())))
      base.setFechaCarga(nuevo.getFechaCarga());
    if (nuevo.getFechaModificacion() != null && (base.getFechaModificacion() == null || nuevo.getFechaModificacion().isAfter(base.getFechaModificacion())))
      base.setFechaModificacion(nuevo.getFechaModificacion());

    // multimedia: merge por (tipo+path) evitando duplicados
    if (nuevo.getMultimedia() != null && !nuevo.getMultimedia().isEmpty()) {
      if (base.getMultimedia() == null) base.setMultimedia(new ArrayList<>());
      var existentes = base.getMultimedia();
      var firmas = existentes.stream()
              .map(m -> (m.getTipoMultimedia() + "|" + m.getPath()))
              .collect(Collectors.toSet());
      nuevo.getMultimedia().forEach(m -> {
        String f = m.getTipoMultimedia() + "|" + m.getPath();
        if (!firmas.contains(f)) {
          existentes.add(m);
          firmas.add(f);
        }
      });
    }

    // metadata: completar claves faltantes, no sobreescribir existentes
    if (nuevo.getMetadata() != null && !nuevo.getMetadata().isEmpty()) {
      if (base.getMetadata() == null) base.setMetadata(new HashMap<>());
      nuevo.getMetadata().forEach((k, v) -> base.getMetadata().putIfAbsent(k, v));
    }

    return base;
  }

  /** === Helpers === */

  private String toComparable(String s) {
    if (s == null) return "";
    String t = s.trim().toLowerCase(Locale.ROOT);
    // quitar tildes
    t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    // colapsar espacios
    return t.replaceAll("\\s+", " ");
  }

  private String limpiarEspacios(String s) {
    if (s == null) return null;
    return s.replaceAll("\\s+", " ");
  }

  private boolean isNullOrBlank(String s) {
    return s == null || s.isBlank();
  }

  private Float redondear(Float valor, int decimales) {
    if (valor == null) return null;
    double factor = Math.pow(10, decimales);
    return (float) (Math.round(valor * factor) / factor);
  }

  /** Diccionario por defecto (opcional) */
  // --- defaultNormalizer debe llamar al ctor (int) ---
  public static Normalizador defaultNormalizer() {
    return new Normalizador(4);
  }
  private static Map<String,String> defaultDict() {
    Map<String, String> dict = new HashMap<>();
    dict.put("incendio", "Incendio");
    dict.put("incendios", "Incendio");
    dict.put("fuego", "Incendio");
    dict.put("llamas", "Incendio");
    dict.put("quemazon", "Incendio");
    dict.put("incendio forestal", "Incendio");
    dict.put("fuego forestal", "Incendio");
    dict.put("inundacion", "Inundación");
    dict.put("inundaciones", "Inundación");
    dict.put("anegamiento", "Inundación");
    dict.put("agua", "Inundación");
    dict.put("protesta", "Protesta");
    dict.put("manifestacion", "Protesta");
    dict.put("marcha", "Protesta");

    // normalizar claves (minúsculas, sin tildes)
    Map<String, String> normalizado = new HashMap<>();
    dict.forEach((k, v) -> {
      String key = java.text.Normalizer.normalize(k.toLowerCase(Locale.ROOT), java.text.Normalizer.Form.NFD)
              .replaceAll("\\p{M}", "");
      normalizado.put(key, v);
    });
    return normalizado;
  }
}
