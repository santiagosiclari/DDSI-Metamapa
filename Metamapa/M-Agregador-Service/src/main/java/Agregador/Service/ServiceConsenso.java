package Agregador.Service;
import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.*;
import Agregador.business.Consenso.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceConsenso {
  private final RepositorioConsenso repositorioConsenso;
  private final RepositorioHechos repositorioHechos;

  @Transactional
  public void consensuarHechos() {
    List<Hecho> hechos = repositorioHechos.findAll();
    List<Consenso> consensos = repositorioConsenso.findAll();
    int cantFuentes = (int) hechos.stream().map(Hecho::getIdFuente).distinct().count();
    Map<String, List<Hecho>> hechosPorClave = hechos.stream()
            .collect(Collectors.groupingBy(this::claveHecho));
    for (Hecho hecho : hechos) {
      List<Hecho> posiblesIguales = hechosPorClave.getOrDefault(claveHecho(hecho), List.of());
      Set<Consenso> nuevosConsensos = new HashSet<>();
      for (Consenso c : consensos) {
        if (!estaConsensuadoPorNombre(hecho, c) && c.esConsensuado(hecho, posiblesIguales, cantFuentes))
          nuevosConsensos.add(c);
      }
      if (!nuevosConsensos.isEmpty()) {
        hecho.getConsensos().addAll(nuevosConsensos);
        System.out.println("Hecho ID " + hecho.getId() + " -> Consensos agregados: " +
                nuevosConsensos.stream()
                        .map(Consenso::getNombreTipo)
                        .collect(Collectors.joining(", ")));
      }
    }
  }

  private boolean estaConsensuadoPorNombre(Hecho hecho, Consenso consenso) {
    return hecho.getConsensos().stream()
            .anyMatch(c -> c.getNombreTipo().equals(consenso.getNombreTipo()));
  }

  private String claveHecho(Hecho h) {
    String lat = h.getLatitud() != null ? String.format("%.3f", h.getLatitud()) : "null";
    String lon = h.getLongitud() != null ? String.format("%.3f", h.getLongitud()) : "null";
    return h.getTitulo().toLowerCase() + "|" + h.getFechaHecho() + "|" + lat + "|" + lon;
  }
}