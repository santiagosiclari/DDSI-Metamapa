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
  private List<CriterioDTO> criteriosPertenencia = new ArrayList<>();
  private List<CriterioDTO> criteriosNoPertenencia = new ArrayList<>();

  public ColeccionDTO() {}

  // Constructor domain -> DTO
  public ColeccionDTO(Coleccion coleccion) {
    this.titulo = coleccion.getTitulo();
    this.descripcion = coleccion.getDescripcion();
    this.handle = coleccion.getHandle();
    this.consenso = Consenso.toString(coleccion.getConsenso()); // ← String
    if (coleccion.getCriterioPertenencia() != null) {
      this.criteriosPertenencia = coleccion.getCriterioPertenencia().stream()
              .map(CriterioDTO::new) // necesitaría un constructor Criterio -> CriterioDTO
              .collect(Collectors.toList());
    }
    if (coleccion.getCriterioNoPertenencia() != null) {
      this.criteriosNoPertenencia = coleccion.getCriterioNoPertenencia().stream()
              .map(CriterioDTO::new)
              .collect(Collectors.toList());
    }
  }
  public Coleccion toDomain() {
    ArrayList<Criterio> pert = this.criteriosPertenencia == null ? new ArrayList<>() :
            this.criteriosPertenencia.stream().map(CriterioDTO::toDomain)
                    .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

    ArrayList<Criterio> noPert = this.criteriosNoPertenencia == null ? new ArrayList<>() :
            this.criteriosNoPertenencia.stream().map(CriterioDTO::toDomain)
                    .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

    return new Coleccion(
            this.titulo,
            this.descripcion,
            Consenso.fromString(this.consenso), // ← crea la estrategia correcta
            pert,
            noPert
    );
  }
}