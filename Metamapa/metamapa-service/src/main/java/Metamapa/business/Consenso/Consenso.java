package Metamapa.business.Consenso;

import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.Hechos.Hecho;
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