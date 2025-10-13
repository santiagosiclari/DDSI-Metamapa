package Agregador.Service;
import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.RepositorioHechos;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ServiceAgregador {
  private final RepositorioHechos repo;
  private final Normalizador normalizador;           // tu clase mejorada

  public ServiceAgregador(RepositorioHechos repo, Normalizador normalizador) {
    this.repo = repo;
    this.normalizador = normalizador;
  }

  // Devuelve el nombre de la categoría con más hechos reportados
  public String categoriaMasReportada() {
    var hechos = repo.findAll();                      // List<Hecho>
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