package Estadistica.persistencia;

import Estadistica.business.Estadistica.Estadistica;

import java.util.Map;
import java.util.Optional;

public interface RepositorioEstadisticasCustom {
  public <T extends Estadistica> Optional<T> obtenerMasNueva(
          Class<T> clazz,
          Map<String, Object> filtros
  );
}
