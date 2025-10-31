package FuenteDinamica.DTO;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import FuenteDinamica.business.Hechos.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
  private String fechaHecho;
  @NotNull
  private Integer idUsuario;
  private Integer fuenteId; // solo para subir hecho...
  private Boolean anonimo = false;
  private Map<String, String> metadata;

  // Método para convertir a dominio
  public Hecho toDomain(FuenteDinamica fuente) {
    Hecho hecho = new Hecho();
    hecho.setTitulo(this.titulo);
    hecho.setDescripcion(this.descripcion);
    hecho.setCategoria(this.categoria);
    hecho.setLatitud(this.latitud);
    hecho.setLongitud(this.longitud);
    hecho.setFechaHecho(LocalDateTime.parse(this.fechaHecho));
    hecho.setIdUsuario(this.idUsuario);
    hecho.setFuente(fuente);
    hecho.setAnonimo(this.anonimo);
    // Inicializar metadata si es null
    if (this.metadata != null) {
      hecho.setMetadata(new HashMap<>(this.metadata));
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
    dto.setFechaHecho( hecho.getFechaHecho().format(DateTimeFormatter.ISO_LOCAL_DATE));
    dto.setIdUsuario(hecho.getIdUsuario());
    dto.setAnonimo(hecho.getAnonimo());
    if (hecho.getMetadata() != null) {
      dto.setMetadata(new HashMap<>(hecho.getMetadata()));
    }
    return dto;
  }
}