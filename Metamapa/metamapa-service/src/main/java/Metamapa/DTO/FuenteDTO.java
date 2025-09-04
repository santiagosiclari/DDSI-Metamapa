package Metamapa.DTO;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuenteDTO {
  // El upstream podría enviar "id" o "fuenteId". Aceptá ambos:
  private Integer fuenteId;

  @JsonProperty("id")
  public void setId(Integer id){ this.fuenteId = id; }

  @JsonProperty("fuenteId")
  public void setFuenteIdAlt(Integer id){ if (id != null) this.fuenteId = id; }

  private String nombre;
  // MUY IMPORTANTE para decidir el subtipo:
  private String tipoFuente; // "dinamica" | "estatica" | "proxy"

  private List<HechoDTO> hechos; // si te sirve; si no, podés omitir
}