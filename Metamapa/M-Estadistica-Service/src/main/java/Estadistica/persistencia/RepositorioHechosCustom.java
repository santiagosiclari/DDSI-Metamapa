package Estadistica.persistencia;

import Estadistica.business.Estadistica.Criterios.Criterio;
import Estadistica.business.Estadistica.Hecho;
import java.util.List;

public interface RepositorioHechosCustom {
  List<Hecho> filtrarPorCriterios(List<Criterio> criterios);
   //List<Criterio> construirCriterios(FiltrosHechosDTO filtros, boolean incluir);
}
