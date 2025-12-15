package Estadistica.business.Estadistica;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Entity
@Table(schema = "stats_schema")
public class CantidadDeSpam extends Estadistica {
  long cantidadSolicitudesSpam;

  public CantidadDeSpam() {
  }

  public CantidadDeSpam(long cantidadDeSpam) {
    super();
    this.cantidadSolicitudesSpam = cantidadDeSpam;
  }
}