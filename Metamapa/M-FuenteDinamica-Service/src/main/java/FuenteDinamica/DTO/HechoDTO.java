package FuenteDinamica.DTO;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import FuenteDinamica.business.Hechos.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class HechoDTO {
  @NotBlank
  private String titulo;
  @NotBlank
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDate fechaHecho;
  @NotNull
  private Integer idUsuario;
  private Boolean anonimo = false;
  private List<MultimediaDTO> multimedia;
  private Map<String, String> metadata;

  // Método para convertir a dominio
  public Hecho toDomain(FuenteDinamica fuente) {
    Hecho hecho = new Hecho();
    hecho.setTitulo(this.titulo);
    hecho.setDescripcion(this.descripcion);
    hecho.setCategoria(this.categoria);
    hecho.setLatitud(this.latitud);
    hecho.setLongitud(this.longitud);
    hecho.setFechaHecho(this.fechaHecho);
    hecho.setIdUsuario(this.idUsuario);
    hecho.setFuente(fuente);
    hecho.setAnonimo(this.anonimo);

    // Inicializar metadata si es null
    if (this.metadata != null) {
      hecho.setMetadata(new HashMap<>(this.metadata));
    }
    // Convertir multimedia
    if (this.multimedia != null) {
      List<Multimedia> lista = this.multimedia.stream()
              .map(m -> {
                Multimedia mm = new Multimedia();
                mm.setPath(m.getPath());
                mm.setTipoMultimedia(TipoMultimedia.valueOf(m.getTipoMultimedia()));
                mm.setHecho(hecho); // 🔑 enlazar al hecho
                return mm;
              }).toList();
      hecho.setMultimedia(lista);
    }
    return hecho;
  }

  public static HechoDTO fromDomain(Hecho hecho) {
    HechoDTO dto = new HechoDTO();
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setCategoria(hecho.getCategoria());
    dto.setLatitud(hecho.getLatitud());
    dto.setLongitud(hecho.getLongitud());
    dto.setFechaHecho(hecho.getFechaHecho());
    dto.setIdUsuario(hecho.getIdUsuario());
    dto.setAnonimo(hecho.getAnonimo());
    if (hecho.getMultimedia() != null) {
      List<MultimediaDTO> multimediaDTO = hecho.getMultimedia().stream()
              .map(MultimediaDTO::new) // 🔹 Usamos el constructor que recibe Multimedia
              .toList();
      dto.setMultimedia(multimediaDTO);
    }
    if (hecho.getMetadata() != null) {
      dto.setMetadata(new HashMap<>(hecho.getMetadata()));
    }
    return dto;
  }
}