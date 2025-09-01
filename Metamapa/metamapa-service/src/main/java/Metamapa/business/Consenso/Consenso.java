package Metamapa.business.Consenso;

import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.Hechos.Hecho;
import java.util.List;

public interface Consenso {
  boolean esConsensuado(Hecho hecho, List<FuenteDeDatos> fuentes);

    public static String toString(Consenso c) {
        if (c == null) return null;
        if (c instanceof Consenso) return "Absoluto";
        if (c instanceof Consenso) return "MultiplesMenciones";
        if (c instanceof Consenso) return "MayoriaSimple";
        return c.toString();
    }
    public static Consenso fromString(String nombre) {
        if (nombre == null) nombre = "MayoriaSimple";
        return switch (nombre) {
            case "Absoluto" -> new Absoluto();
            case "MultiplesMenciones" -> new MultiplesMenciones();
            case "MayoriaSimple" -> new MayoriaSimple();
            default -> new MayoriaSimple();
        };
    }
}