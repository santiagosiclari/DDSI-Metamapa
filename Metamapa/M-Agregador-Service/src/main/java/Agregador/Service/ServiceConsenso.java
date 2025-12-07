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
    ArrayList<Hecho> hechos = (ArrayList<Hecho>) repositorioHechos.findAll();
    List<Consenso> consensos = repositorioConsenso.findAll();

    for (Hecho hecho : hechos) {
      // Filtra solo los consensos que aún no están aplicados y son válidos
      Set<Consenso> nuevosConsensos = consensos.stream()
              .filter(c -> !estaConsensuadoPorNombre(hecho, c) && c.esConsensuado(hecho, hechos))
              .collect(Collectors.toSet());
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
}