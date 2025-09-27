package FuenteDinamica.DTO;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import FuenteDinamica.business.Hechos.Hecho;
import lombok.*;
import java.util.List;

@Getter @Setter
public class FuenteDTO {
  private Integer id;
  private String nombre;
  private List<Integer> hechosIds;

  public FuenteDTO(FuenteDinamica fuente) {
    this.id = fuente.getFuenteId();
    this.nombre = fuente.getNombre();
    this.hechosIds = fuente.getHechos().stream()
            .map(Hecho::getId)
            .toList();
  }
}
