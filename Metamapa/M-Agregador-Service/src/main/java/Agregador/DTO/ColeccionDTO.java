package Agregador.DTO;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.Consenso;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class ColeccionDTO {
  @NotEmpty @NotBlank @NotNull
  private String titulo;
  @NotEmpty @NotBlank @NotNull
  private String descripcion;
  private UUID handle;
  private String consenso;
  private List<CriterioDTO> criterios = new ArrayList<>();

  public ColeccionDTO() {}

  // Constructor domain -> DTO
  public ColeccionDTO(Coleccion coleccion) {
    this.titulo = coleccion.getTitulo();
    this.descripcion = coleccion.getDescripcion();
    this.handle = coleccion.getHandle();
    this.consenso = Consenso.toString(coleccion.getConsenso()); // ← String
    if (coleccion.getCriterios() != null) {
      this.criterios = coleccion.getCriterios().stream()
          .map(CriterioDTO::new) // necesitaría un constructor Criterio -> CriterioDTO
          .collect(Collectors.toList());
    }
  }

  public Coleccion toDomain() {
    ArrayList<Criterio> criterios = this.criterios == null ? new ArrayList<>() :
        this.criterios.stream().map(CriterioDTO::toDomain)
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    return new Coleccion(
            this.titulo,
            this.descripcion,
            Consenso.fromString(this.consenso), // ← crea la estrategia correcta
            criterios
    );
  }
}