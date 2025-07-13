package domain.business.Consenso;

import DTO.FuenteDeDatosDTO;
import DTO.HechoDTO;
import domain.business.FuentesDeDatos.FuenteDeDatos;
import domain.business.incidencias.Hecho;
import java.util.List;

public interface Consenso {
  boolean esConsensuado(Hecho hecho, List<FuenteDeDatos> fuentes);

  static Consenso stringToConsenso(String algoritmo) {
        switch (algoritmo)
        {
          case "Absoluto": return new Absoluto();
          case "MultiplesMenciones": return new MultiplesMenciones();
          case "MayoriaSimple": return new MayoriaSimple();
          default: throw new IllegalArgumentException("Tipo de consenso no valido");
        }
  }
}