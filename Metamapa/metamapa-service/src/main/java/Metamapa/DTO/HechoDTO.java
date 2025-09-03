package Metamapa.DTO;

import Metamapa.business.Hechos.Multimedia;
import Metamapa.business.Hechos.TipoMultimedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class HechoDTO {

  // ——— Campos principales ———
  @NotBlank
  private String titulo;

  private String descripcion;
  private String categoria;

  // Si mandás coordenadas, idealmente ambas.
  private Float latitud;
  private Float longitud;

  // yyyy-MM-dd en JSON
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate fechaHecho;

  // Si anonimo = true, ignorá autor en el Controller/Service
  @Size(max = 100)
  private String autor;

  private Boolean anonimo = Boolean.FALSE;

  // Lista de multimedia (opcional)
  private List<MultimediaItem> multimedia = new ArrayList<>();

  // ——— Helper: convierte a la lista de Multimedia del dominio ———
  public List<Multimedia> toMultimediaDomain() {
    if (multimedia == null) return List.of();
    List<Multimedia> out = new ArrayList<>();
    for (MultimediaItem it : multimedia) {
      if (it == null || it.getTipoMultimedia() == null || it.getPath() == null || it.getPath().isBlank()) continue;
      Multimedia m = new Multimedia();
      m.setTipoMultimedia(it.getTipoMultimedia());
      m.setPath(it.getPath().trim());
      out.add(m);
    }
    return out;
  }

  // ——— Validación simple de par lat/lon ———
  public void validarCoordenadas() {
    if ((latitud == null) ^ (longitud == null)) {
      throw new IllegalArgumentException("Si enviás coordenadas, deben incluir latitud y longitud.");
    }
  }

  // DTO interno para multimedia
  @Getter @Setter
  public static class MultimediaItem {
    private TipoMultimedia tipoMultimedia; // IMAGEN, VIDEO, AUDIO, PDF, etc.
    private String path;                   // URL o ruta
  }
}
