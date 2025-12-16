package Estadistica.business.Estadistica;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "stats_schema")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Estadistica {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  int id;
  @Getter
  LocalDateTime fechaDeMedicion;

  public Estadistica() {
      this.fechaDeMedicion = LocalDateTime.now();
  }
}