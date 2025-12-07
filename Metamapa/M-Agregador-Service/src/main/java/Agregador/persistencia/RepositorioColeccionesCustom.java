package Agregador.persistencia;

import Agregador.business.Colecciones.Coleccion;
import Agregador.business.Colecciones.Criterio;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioColeccionesCustom {
  List<Criterio> getCriteriosColeccion(UUID id);
  Optional<Coleccion> getColeccion(UUID id);
}
