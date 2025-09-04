package Metamapa.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// DTO para el request
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEliminacionDTO {
  @NotBlank
  private Integer idHechoAfectado;

  @NotBlank
  @Size(min = 500, message = "El motivo debe tener al menos 500 caracteres")
  private String motivo;

  private String url; // opcional
}
