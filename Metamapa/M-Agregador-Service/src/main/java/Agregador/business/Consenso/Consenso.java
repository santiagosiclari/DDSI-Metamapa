package Agregador.business.Consenso;

import java.util.ArrayList;
import java.util.Objects;
import Agregador.business.Agregador.*;
import Agregador.business.Colecciones.*;
import Agregador.business.Hechos.*;

public interface Consenso {
  boolean esConsensuado(Hecho hecho);

  //TODO reimplementar los consensos sin la clase fuente de datos
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

  static Boolean sonIguales(Hecho hecho1, Hecho hecho2) {
    return hecho1.getFechaHecho() == hecho2.getFechaHecho() &&
            Math.abs(hecho1.getLatitud() - hecho2.getLatitud()) < 10  &&
            Math.abs(hecho1.getLongitud() - hecho2.getLongitud()) < 10 &&
            hecho1.getTitulo().equalsIgnoreCase(hecho2.getTitulo()) &&
            hecho1.getTitulo().equals(hecho2.getTitulo());
  }

  //ABSOLUTA
  // "SELECT * FROM hechos as Hecho1 where (select count(distinct left(Hechos1.hechos_id,4)) from hechos) = (select count(distinct left(hechos_id,4)) from hechos as Hecho2 where equals(Hecho1,Hecho2))"

  //MAYORIA SIMPLE
  // "SELECT * FROM hechos as Hecho1 where (select count(distinct left(Hechos1.hechos_id,4)) from hechos)/2 =< (select count(distinct left(hechos_id,4)) from hechos as Hecho2 where equals(Hecho1,Hecho2)) "

  //MULTIPLES MENCIONES
  /*select titulo, latitud, longitud, etc
    from hechos h
    group by titulo, latitud, longitud, etc
    having
          count(distinct fuente_id) >= 2
          and not exists(
                  select 1 from hechos h2
                  where h2.titulo = h.titulo
                    and (h2.latitud != h.latitud or h2.longitud != h.longitud or ... )
  );*/

  // "SELECT * FROM hechos as Hecho1 where (select count(distinct left(Hechos1.hechos_id,4)) from hechos as Hecho2 where equals(Hecho1,Hecho2)) > 1 and (select count(*) from hechos as Hecho2 where Hecho1.titulo = Hecho2.titulo and todo lo demas distinto) "

}