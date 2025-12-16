package Agregador.Service;
import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceAgregador {
  private final RepositorioHechos repo;

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
    List<Hecho> hechos = repo.findByCategoriaIgnoreCaseAndEliminadoFalse(categoria);
    Map<Integer, Long> conteoPorHora = hechos.stream()
            .map(this::horaDelHecho)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(h -> h, Collectors.counting()));
    return conteoPorHora.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
  }

  private Integer horaDelHecho(Hecho h) {
    if (h.getMetadata() != null) {
      String hh = h.getMetadata().get("hora");
      if (hh != null) {
        try {
          return java.time.LocalTime.parse(hh).getHour();
        } catch (Exception ignore) {
        }
        try {
          int x = Integer.parseInt(hh);
          if (0 <= x && x <= 23) return x;
        } catch (Exception ignore) {
        }
      }
    }
    return null;
  }
}