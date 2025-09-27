package FuenteDinamica.DTO;
import FuenteDinamica.business.Hechos.*;
import lombok.*;

@Getter @Setter
public class MultimediaDTO {
  private String tipoMultimedia;
  private String path;

  public MultimediaDTO(Multimedia multimedia) {
    this.tipoMultimedia = multimedia.getTipoMultimedia().name();
    this.path = multimedia.getPath();
  }

  public MultimediaDTO() {}

  public Multimedia toDomain() {
    return new Multimedia(
            TipoMultimedia.valueOf(tipoMultimedia),
            path
    );
  }
}