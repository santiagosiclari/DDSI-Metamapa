package FuenteDinamica.business.FuentesDeDatos;
import FuenteDinamica.business.Hechos.*;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
import jakarta.persistence.*;

@JsonTypeName("FUENTEDINAMICA")
@Entity
@Table(name = "fuente_dinamica")
@Getter @Setter
public class FuenteDinamica {
  //static private Integer contadorID = 1000000;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer fuenteId;  // Hibernate lo maneja, no necesitamos contador manual
  private String nombre;
  @OneToMany(mappedBy = "fuente", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Hecho> hechos = new ArrayList<>();

  public FuenteDinamica() {
    this.nombre = "Fuente Dinamica";
  }

  public void agregarHecho(
          String titulo,
          String desc,
          String categoria,
          Float latitud,
          Float longitud,
          LocalDate fechaHecho,
          Integer idAutor,
          Boolean anonimidad,
          List<Multimedia> multimedia) {
    Hecho h = new Hecho(
            titulo,
            desc,
            categoria,
            latitud,
            longitud,
            fechaHecho,
            idAutor,
            this,
            anonimidad,
            multimedia
    );
    this.hechos.add(h);
    if (multimedia != null) {
      for (Multimedia m : multimedia) {
        m.setHecho(h); // importante para persistir la relación
      }
    }
  }
}