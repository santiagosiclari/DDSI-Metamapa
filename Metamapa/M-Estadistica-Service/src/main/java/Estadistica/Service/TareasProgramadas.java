package Estadistica.Service;

import Estadistica.web.ControllerEstadistica;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
   public class TareasProgramadas {
     private final ControllerEstadistica controllerEstadistica;
     private final MeterRegistry registry;

     @Scheduled(fixedRate = 60 * 60 * 1000) //TODO: revisar tiempo
     public void actualizarEstadisticas() {
       controllerEstadistica.actualizarEstadisticas();
     }

     @Scheduled(fixedRate = 30 * 60 * 1000) //TODO: revisar tiempo
     public void actualizarEstadisticaUI() {
       var counter = registry.find("http.server.requests.count").counter();
       double requests = (counter != null) ? counter.count() : 0.0;
       if (requests < 100) {
         controllerEstadistica.actualizarEstadisticaUI();
       }
     }
}