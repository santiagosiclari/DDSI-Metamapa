package Agregador.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccionSolicitudDTO {
  private String accion; // "APROBAR" o "RECHAZAR"
}