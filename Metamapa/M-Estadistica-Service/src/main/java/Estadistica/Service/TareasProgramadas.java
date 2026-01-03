package Estadistica.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TareasProgramadas {
    private final ServiceEstadistica serviceEstadistica;

    @Scheduled(fixedRate = 30 * 60 * 1000)
    @Transactional
    public void recalcularEstadisticas() {
        serviceEstadistica.actualizar();
    }
}
