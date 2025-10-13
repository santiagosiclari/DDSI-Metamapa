package Agregador.business.Consenso;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.*;
import java.util.ArrayList;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "descripcion", discriminatorType = DiscriminatorType.STRING)
public abstract class Consenso {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer id;

  public boolean esConsensuado(Hecho hecho, ArrayList<Hecho> hechos) {
    return true;
  };

  public static String toString(Consenso c) {
    if (c == null) return null;
    if (c instanceof Absoluto) return "Absoluto";
    if (c instanceof MultiplesMenciones) return "MultiplesMenciones";
    if (c instanceof MayoriaSimple) return "MayoriaSimple";
    return c.getClass().getSimpleName();
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

  public static Boolean sonIguales(Hecho hecho1, Hecho hecho2) {
    return hecho1.getFechaHecho() == hecho2.getFechaHecho() &&
            Math.abs(hecho1.getLatitud() - hecho2.getLatitud()) < 10  &&
            Math.abs(hecho1.getLongitud() - hecho2.getLongitud()) < 10 &&
            hecho1.getTitulo().equalsIgnoreCase(hecho2.getTitulo()) &&
            hecho1.getTitulo().equals(hecho2.getTitulo());
  }


  static public int contarFuentesDeDatos(ArrayList<Hecho> hechos) {
    ArrayList<Integer> fuentes = new ArrayList<>();
    int contador = 0;
    for (Hecho hecho : hechos) {
        if(!fuentes.contains(hecho.getIdFuente())) {
          contador++;
          fuentes.add(hecho.getIdFuente());
        }
    }
    return contador;
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

  // "SELECT * FROM hechos as Hecho1
  // where (select count(distinct left(Hechos1.hechos_id,4)) from hechos as Hecho2 where equals(Hecho1,Hecho2)) > 1 and (select count(*) from hechos as Hecho2 where Hecho1.titulo = Hecho2.titulo and todo lo demas distinto) "
}