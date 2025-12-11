package FuenteMetamapa.DTO;
import FuenteMetamapa.business.Hechos.Hecho;
import FuenteMetamapa.business.FuentesDeDatos.FuenteMetamapa;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.*;

@Setter @Getter
public class HechoDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private String fechaHecho;
  private LocalDateTime fechaModificacion;
  private Integer fuenteId;
  private Integer id;

  public HechoDTO(){}

  public HechoDTO(Hecho hecho) {
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria();
    this.latitud = hecho.getLatitud();
    this.longitud = hecho.getLongitud();
    this.id = hecho.getId();
    this.fuenteId = hecho.getFuente().getId();
    this.fechaHecho = hecho.getFechaHecho().format(DateTimeFormatter.ISO_LOCAL_DATE);
    this.fechaModificacion = hecho.getFechaModificacion();
  }

  public Hecho toDomain(FuenteMetamapa fuente) {
    Hecho hecho = new Hecho();
    hecho.setTitulo(this.titulo);
    hecho.setDescripcion(this.descripcion);
    hecho.setCategoria(this.categoria);
    hecho.setLatitud(this.latitud);
    hecho.setLongitud(this.longitud);
    hecho.setFechaHecho(LocalDateTime.parse(this.fechaHecho));
    hecho.setFuente(fuente);
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
    return dto;
  }
}
