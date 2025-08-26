package Agregador.DTO;
import Agregador.business.Colecciones.Coleccion;
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
    this.consenso = coleccion.getConsenso() != null
            ? coleccion.getConsenso().getClass().getSimpleName()
            : null;
    if (coleccion.getCriterioPertenencia() != null) {
      this.criteriosPertenencia = coleccion.getCriterioPertenencia().stream()
              .map(CriterioDTO::new) // necesitarías un constructor Criterio -> CriterioDTO
              .collect(Collectors.toList());
    }
    if (coleccion.getCriterioNoPertenencia() != null) {
      this.criteriosNoPertenencia = coleccion.getCriterioNoPertenencia().stream()
              .map(CriterioDTO::new)
              .collect(Collectors.toList());
    }
  }
}