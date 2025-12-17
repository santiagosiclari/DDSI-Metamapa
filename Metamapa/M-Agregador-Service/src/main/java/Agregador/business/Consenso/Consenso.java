package Agregador.business.Consenso;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Consenso {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  public Integer id;
  @Column
  @Setter @Getter
  private String nombreTipo;

  public Consenso() {}

  public Consenso(String nombreTipo) {
    this.nombreTipo = nombreTipo;
  }

  public abstract boolean esConsensuado(Hecho hecho, List<Hecho> hechos, int cantFuentes);

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

  public static boolean sonIguales(Hecho h1, Hecho h2) {
    if (h1 == null || h2 == null) return false;
    boolean mismaFecha = h1.getFechaHecho().equals(h2.getFechaHecho());
    boolean latitudCercana = Math.abs(h1.getLatitud() - h2.getLatitud()) < 0.001;  // ~100 metros
    boolean longitudCercana = Math.abs(h1.getLongitud() - h2.getLongitud()) < 0.001;
    boolean mismoTitulo = h1.getTitulo().equalsIgnoreCase(h2.getTitulo());
    return mismaFecha && latitudCercana && longitudCercana && mismoTitulo;
  }

}