package Agregador.Service;
import Agregador.business.Hechos.Hecho;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import java.text.Normalizer;
import java.time.LocalDateTime;
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
  private final int decimalesCoordenadas;
  private static final JaroWinklerSimilarity SIMILARITY = new JaroWinklerSimilarity();

  public Normalizador(@Value("${normalizador.decimales:4}") int decimalesCoordenadas) {
    this.decimalesCoordenadas = decimalesCoordenadas;
  }

  private static final Map<String, String> PATRONES = Map.<String, String>ofEntries(
          // --- Incendios ---
          Map.entry("incendio", "Incendio"),
          Map.entry("fuego", "Incendio"),
          Map.entry("quema", "Incendio"),
          Map.entry("llamas", "Incendio"),
          Map.entry("explosion seguida de incendio", "Incendio"),
          // --- Explosiones ---
          Map.entry("explosion", "Explosión"),
          Map.entry("detonacion", "Explosión"),
          Map.entry("deflagracion", "Explosión"),
          // --- Gas / Sustancias ---
          Map.entry("gas", "Fuga o emanación de gas"),
          Map.entry("escape de gas", "Fuga o emanación de gas"),
          Map.entry("fuga", "Fuga o emanación de gas"),
          Map.entry("emanacion", "Fuga o emanación de gas"),
          Map.entry("perdida de gas", "Fuga o emanación de gas"),
          Map.entry("derrame quimico", "Accidente químico"),
          Map.entry("accidente quimico", "Accidente químico"),
          Map.entry("emanacion toxica", "Accidente químico"),
          // --- Transporte ---
          Map.entry("descarrilamiento", "Accidente ferroviario"),
          Map.entry("avion", "Accidente aéreo"),
          Map.entry("aeronave", "Accidente aéreo"),
          Map.entry("transporte publico", "Accidente de transporte"),
          Map.entry("paso a nivel", "Accidente de transporte"),
          Map.entry("choque en cadena", "Siniestro vial"),
          Map.entry("colision", "Siniestro vial"),
          Map.entry("volcamiento", "Siniestro vial"),
          Map.entry("atropello", "Siniestro vial"),
          // --- Industrial ---
          Map.entry("maquinaria industrial", "Accidente industrial"),
          Map.entry("planta procesadora", "Accidente industrial"),
          Map.entry("fabrica", "Accidente industrial"),
          Map.entry("explosion industrial", "Accidente industrial"),
          Map.entry("fallo en sistema de seguridad industrial", "Accidente industrial"),
          // --- Derrumbes / Deslizamientos ---
          Map.entry("derrumbe", "Deslizamiento o derrumbe"),
          Map.entry("aluvion", "Deslizamiento o derrumbe"),
          Map.entry("deslizamiento", "Deslizamiento o derrumbe"),
          // --- Meteorológicos ---
          Map.entry("viento", "Viento fuerte"),
          Map.entry("rafaga", "Viento fuerte"),
          Map.entry("huracan", "Viento fuerte"),
          Map.entry("tormenta", "Tormenta"),
          Map.entry("granizo", "Granizo"),
          Map.entry("lluvia", "Lluvia"),
          Map.entry("precipitacion", "Lluvia"),
          Map.entry("inundacion", "Inundación"),
          Map.entry("anegamiento", "Inundación"),
          Map.entry("torrente", "Inundación"),
          // --- Energía / Infraestructura ---
          Map.entry("apagon", "Corte de energía"),
          Map.entry("corte de luz", "Corte de energía"),
          Map.entry("fallo electrico", "Fallo de infraestructura"),
          Map.entry("red de distribucion", "Fallo de infraestructura"),
          Map.entry("sistema de seguridad", "Fallo de infraestructura"),
          // --- Salud / Sanitario ---
          Map.entry("epidemia", "Emergencia sanitaria"),
          Map.entry("virus", "Emergencia sanitaria"),
          Map.entry("infeccion", "Emergencia sanitaria"),
          Map.entry("intoxicacion", "Emergencia sanitaria"),
          // --- Otros / Contaminación ---
          Map.entry("contaminacion", "Contaminación"),
          Map.entry("planta industrial", "Accidente industrial"),
          Map.entry("contaminación ambiental", "Contaminación"),
          Map.entry("vertido contaminante", "Contaminación"),
          Map.entry("polución industrial", "Contaminación"),
          Map.entry("crisis ambiental por contaminantes", "Contaminación"),
          Map.entry("derrame en curso de agua", "Contaminación"),
          Map.entry("impacto de contaminantes", "Contaminación"),
          // Emergencia sanitaria
          Map.entry("crisis sanitaria", "Emergencia sanitaria"),
          Map.entry("propagación de enfermedad", "Emergencia sanitaria"),
          Map.entry("brote epidémico", "Emergencia sanitaria"),
          Map.entry("emergencia de salud pública", "Emergencia sanitaria"),
          Map.entry("casos agrupados de enfermedad", "Emergencia sanitaria"),
          Map.entry("brote de enfermedad contagiosa", "Emergencia sanitaria"),
          // Derrames / Fugas
          Map.entry("fuga de material peligroso", "Derrame / Fuga de sustancias"),
          Map.entry("vertido de químicos", "Derrame / Fuga de sustancias"),
          Map.entry("derrame", "Derrame / Fuga de sustancias"),
          Map.entry("derrames de sustancias químicas", "Derrame / Fuga de sustancias"),
          // Intoxicación masiva
          Map.entry("intoxicación alimentaria masiva", "Intoxicación masiva"),
          Map.entry("intoxicación por consumo", "Intoxicación masiva"),
          Map.entry("intoxicación por sustancias químicas", "Intoxicación masiva"),
          Map.entry("casos múltiples de intoxicación", "Intoxicación masiva"),
          // --- Material volcánico ---
          Map.entry("precipitación de material volcánico", "Material volcánico"),
          Map.entry("emisión volcánica", "Material volcánico"),
          Map.entry("polvo volcánico en suspensión", "Material volcánico"),
          // --- Sequía / Escasez de agua ---
          Map.entry("sequía extrema", "Sequía"),
          Map.entry("sequía con pérdidas agrícolas", "Sequía"),
          Map.entry("escasez de agua", "Escasez de agua"),
          // --- Tormentas y fenómenos meteorológicos ---
          Map.entry("tormenta con piedras de granizo", "Tormenta / Granizo"),
          Map.entry("tormenta de granizo", "Tormenta / Granizo"),
          Map.entry("tormenta de nieve", "Tormenta de nieve"),
          Map.entry("vendaval", "Viento fuerte"),
          Map.entry("temporal de viento", "Viento fuerte"),
          Map.entry("vientos huracanados", "Viento huracanado"),
          Map.entry("vientos con fuerza ciclónica", "Viento huracanado"),
          // --- Inundaciones / Anegamiento ---
          Map.entry("anegamiento masivo", "Inundación"),
          Map.entry("desborde de río", "Inundación"),
          Map.entry("desborde de arroyo", "Inundación"),
          Map.entry("inundación por lluvias intensas", "Inundación"),
          // --- Temperaturas extremas ---
          Map.entry("frío extremo", "Temperatura extrema"),
          Map.entry("ola de calor extremo", "Temperatura extrema"),
          Map.entry("emergencia por altas temperaturas", "Temperatura extrema"),
          // --- Sismos / Terremotos ---
          Map.entry("sismo de gran magnitud", "Sismo / Terremoto"),
          Map.entry("terremoto destructivo", "Sismo / Terremoto"),
          // --- Fuego forestal ---
          Map.entry("fuego en bosque nativo", "Incendio forestal"),
          Map.entry("incendio forestal", "Incendio forestal"),
          Map.entry("incendio en zona de monte", "Incendio forestal")
  );
  /** === API principal === */
  public List<Hecho> normalizarYUnificar(List<Hecho> hechos) {
    if (hechos == null || hechos.isEmpty()) return List.of();
    List<Hecho> normalizados = hechos.parallelStream()
            .map(this::normalizarHecho)
            .toList();
    Map<String, Hecho> porClave = new HashMap<>();
    normalizados.forEach(h -> {
      String key = clave(h);
      porClave.merge(key, h, this::mergeHechos);
    });
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
    if (h.getLatitud() != null)  h.setLatitud(h.getLatitud());
    if (h.getLongitud() != null) h.setLongitud(h.getLongitud());
    // Fechas: asegurar consistencia básica (si viene nula, no forzar)
    if (h.getFechaCarga() == null)        h.setFechaCarga(LocalDateTime.now());
    if (h.getFechaModificacion() == null) h.setFechaModificacion(LocalDateTime.now());
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
  private final Map<String, String> categoriaCache = new HashMap<>();
  public String normalizarCategoria(String categoriaCruda) {
    if (categoriaCruda == null || categoriaCruda.isBlank()) return "Desconocido";
    String clave = toComparable(categoriaCruda);
    if (categoriaCache.containsKey(clave)) return categoriaCache.get(clave);
    String resultado;
    resultado = PATRONES.get(clave);
    if (resultado == null) {
      for (Map.Entry<String, String> entrada : PATRONES.entrySet()) {
        if (clave.contains(entrada.getKey())) {
          resultado = entrada.getValue();
          break;
        }
      }
    }
    if (resultado == null) resultado = buscarSimilitud(clave);
    if ("Desconocido".equals(resultado)) resultado = generarFallback(categoriaCruda);
    categoriaCache.put(clave, resultado);
    return resultado;
  }

  private String buscarSimilitud(String clave) {
    double mejorScore = 0.7;
    String result = "Desconocido";
    for (Map.Entry<String, String> entry : PATRONES.entrySet()) {
      double score = SIMILARITY.apply(clave, entry.getKey());
      if (score > mejorScore) {
        mejorScore = score;
        result = entry.getValue();
      }
    }
    return result;
  }

  private String generarFallback(String categoriaCruda) {
    String claveNorm = java.text.Normalizer
            .normalize(categoriaCruda, java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
    String resultado = Arrays.stream(claveNorm.split("\\s+"))
            .filter(p -> !p.isBlank())
            .sorted((a, b) -> Integer.compare(b.length(), a.length()))
            .limit(2)
            .collect(Collectors.joining(" "));
    return resultado.isBlank() ? "Desconocido" : resultado;
  }

  /** === Dedupe key === */
  private String clave(Hecho h) {
    StringBuilder keyBuilder = new StringBuilder();
    keyBuilder.append(toComparable(Optional.ofNullable(h.getTitulo()).orElse("")));
    keyBuilder.append("|").append(Optional.ofNullable(h.getFechaHecho()).map(LocalDateTime::toString).orElse("0000-00-00"));
    keyBuilder.append("|").append(Optional.ofNullable(h.getLatitud()).map(lat -> String.format(Locale.ROOT, "%." + decimalesCoordenadas + "f", lat)).orElse("null"));
    keyBuilder.append("|").append(Optional.ofNullable(h.getLongitud()).map(lon -> String.format(Locale.ROOT, "%." + decimalesCoordenadas + "f", lon)).orElse("null"));
    keyBuilder.append("|").append(Optional.ofNullable(h.getIdFuente()).map(Object::toString).orElse("null"));
    return keyBuilder.toString();
  }

  /** === Merge de dos hechos duplicados === */
  private Hecho mergeHechos(Hecho base, Hecho nuevo) {
    // Solo actualizar cuando el valor realmente cambie
    if (!isNullOrBlank(base.getDescripcion()) && isNullOrBlank(nuevo.getDescripcion())) base.setDescripcion(nuevo.getDescripcion());
    if (!"desconocido".equalsIgnoreCase(Optional.ofNullable(base.getCategoria()).orElse("desconocido")) && !isNullOrBlank(nuevo.getCategoria()))
      base.setCategoria(nuevo.getCategoria());
    if (base.getLatitud() == null && nuevo.getLatitud() != null) base.setLatitud(nuevo.getLatitud());
    if (base.getLongitud() == null && nuevo.getLongitud() != null) base.setLongitud(nuevo.getLongitud());
    if (base.getFechaHecho() == null && nuevo.getFechaHecho() != null) base.setFechaHecho(nuevo.getFechaHecho());
    base.setAnonimo(Boolean.TRUE.equals(base.getAnonimo()) || Boolean.TRUE.equals(nuevo.getAnonimo()));
    base.setEliminado(Boolean.TRUE.equals(base.getEliminado()) || Boolean.TRUE.equals(nuevo.getEliminado()));
    if (nuevo.getFechaCarga() != null && (base.getFechaCarga() == null || nuevo.getFechaCarga().isBefore(base.getFechaCarga())))
      base.setFechaCarga(nuevo.getFechaCarga());
    if (nuevo.getFechaModificacion() != null && (base.getFechaModificacion() == null || nuevo.getFechaModificacion().isAfter(base.getFechaModificacion())))
      base.setFechaModificacion(nuevo.getFechaModificacion());
    if (nuevo.getMultimedia() != null && !nuevo.getMultimedia().isEmpty()) {
      if (base.getMultimedia() == null) base.setMultimedia(new ArrayList<>());
      Set<String> firmas = base.getMultimedia().stream()
              .map(m -> m.getTipoMultimedia() + "|" + m.getPath())
              .collect(Collectors.toSet());
      nuevo.getMultimedia().stream()
              .filter(m -> !firmas.contains(m.getTipoMultimedia() + "|" + m.getPath()))
              .forEach(m -> base.getMultimedia().add(m));
    }
    // Metadata: solo agregar claves faltantes
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
}