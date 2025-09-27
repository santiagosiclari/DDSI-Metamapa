package FuenteDinamica.DTO;
import FuenteDinamica.business.Hechos.Hecho;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class HechoDTOResponse {
  private Integer id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDate fechaHecho;
  private Integer idUsuario;
  private Boolean anonimo;
  private Integer fuenteId; // solo el ID de la fuente
  private List<MultimediaDTO> multimedia;

  public HechoDTOResponse(Hecho hecho) {
    this.id = hecho.getId();
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria();
    this.latitud = hecho.getLatitud();
    this.longitud = hecho.getLongitud();
    this.fechaHecho = hecho.getFechaHecho();
    this.idUsuario = hecho.getIdUsuario();
    this.anonimo = hecho.getAnonimo();
    this.fuenteId = hecho.getFuente().getFuenteId(); // solo el id
    this.multimedia = hecho.getMultimedia().stream()
            .map(MultimediaDTO::new)
            .toList();
  }
}