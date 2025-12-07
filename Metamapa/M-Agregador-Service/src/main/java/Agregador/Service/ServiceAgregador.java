package Agregador.Service;
import Agregador.DTO.FiltrosHechosDTO;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceAgregador {
  private final RepositorioHechos repo;
  private final RepositorioHechosCustom repositorioHechos;
  private final Normalizador normalizador;
  private static final int LIMITE_POR_DEFECTO = 500;

  /**
   * Obtiene hechos aplicando Búsqueda por Texto Libre (prioridad alta) o Criterios dinámicos.
   * * @param textoBusqueda Texto libre para buscar en título/descripción (si no es nulo, tiene prioridad).
   * @param filtros DTO con los filtros dinámicos (categoría, fecha, ubicación, etc.).
   * @return Lista de hechos filtrados/buscados.
   */
  public List<Hecho> obtenerHechos(String textoBusqueda, FiltrosHechosDTO filtros) {
    // 1. PRIORIDAD: BÚSQUEDA POR TEXTO LIBRE
    if (textoBusqueda != null && !textoBusqueda.isBlank()) {
      // Llama al método que usa el LIKE o Full-Text Search
      return repositorioHechos.buscarPorTextoLibre(textoBusqueda);
    }
    // 2. APLICAR FILTROS DINÁMICOS (si no hay texto libre)
    // Se asume que FiltrosHechosDTO tiene un método para verificar si hay contenido relevante
    if (filtros != null) {
      // Construir los criterios de INCLUSIÓN (P) y EXCLUSIÓN (NP)
      List<Criterio> criteriosInclusion = repositorioHechos.construirCriterios(filtros, true);
      List<Criterio> criteriosExclusion = repositorioHechos.construirCriterios(filtros, false);

      List<Criterio> todosLosCriterios = new ArrayList<>();
      todosLosCriterios.addAll(criteriosInclusion);
      todosLosCriterios.addAll(criteriosExclusion);
      if (!todosLosCriterios.isEmpty()) {
        // Llamamos a la función de filtrado existente. El consenso es nulo porque no es una colección.
        return repositorioHechos.filtrarPorCriterios(todosLosCriterios, null);
      }
    }
    // 3. 🛑 SOLUCIÓN AL PROBLEMA: DEVOLVER CON LÍMITE (Paginación por Defecto)
    // Usamos PageRequest para pedir solo los primeros 500 hechos (página 0)
    Pageable pageable = PageRequest.of(0, LIMITE_POR_DEFECTO);
    // 🚨 NOTA: Debes asegurarte de que tu repositorio tenga un método findAll(Pageable)
    return repositorioHechos.findAll(pageable).getContent();
  }

  // Devuelve el nombre de la categoría con más hechos reportados
  public String categoriaMasReportada() {
    List<Hecho> hechos = repo.findAll();
    if (hechos.isEmpty()) return null;
    return hechos.stream()
            .map(Hecho::getCategoria)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
  }

  public Integer horaMasReportada(String categoria) {
    // Traé los hechos de esa categoría (y NO eliminados, si aplica)
    List<Hecho> hechos = repo.findByCategoriaIgnoreCaseAndEliminadoFalse(categoria);
    // Extraé la hora (0–23) de cada hecho
    Map<Integer, Long> conteoPorHora = hechos.stream()
            .map(this::horaDelHecho)                  // Integer 0..23 o null
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(h -> h, Collectors.counting()));
    // Tomá la hora con mayor frecuencia (modo)
    return conteoPorHora.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null); // si no hay datos con hora
  }

  /**
   * Extrae la hora del hecho.
   * Recomendado: migrar Hecho.fechaHecho -> LocalDateTime.
   * Provisorio: intentar por metadata["hora"] en formato HH:mm o HH.
   */
  private Integer horaDelHecho(Hecho h) {
    // 1) Si migraste a LocalDateTime:
    // if (h.getFechaHechoDateTime() != null) return h.getFechaHechoDateTime().getHour();
    // 2) Mientras tanto, intentar metadata["hora"]
    if (h.getMetadata() != null) {
      String hh = h.getMetadata().get("hora");
      if (hh != null) {
        try {
          // HH:mm
          return java.time.LocalTime.parse(hh).getHour();
        } catch (Exception ignore) {
        }
        try {
          // HH directo
          int x = Integer.parseInt(hh);
          if (0 <= x && x <= 23) return x;
        } catch (Exception ignore) {
        }
      }
    }
    // Sin hora disponible
    return null;
  }
}