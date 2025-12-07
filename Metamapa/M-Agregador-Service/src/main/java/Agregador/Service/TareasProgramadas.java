package Agregador.Service;
import Agregador.web.ControllerAgregador;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TareasProgramadas {
  private final ControllerAgregador controllerAgregador;
  private final MeterRegistry registry;

  @Scheduled(fixedRate = 60 * 60 * 1000) // se hace al arranque y luego cada 60 minutos
  public void actualizarHechos() {
    controllerAgregador.actualizarHechos();
  }

  @Scheduled(fixedRate = 30 * 60 * 1000, initialDelay = 30 * 60 * 1000) // cada 30 minutos después del arranque
  public void consensuarHechos() {
    var counter = registry.find("http.server.requests.count").counter();
    double requests = (counter != null) ? counter.count() : 0.0;
    if (requests < 100) {
      controllerAgregador.consensuarHechos();
    }
  }
}