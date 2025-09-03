package Metamapa.DTO;

import Metamapa.business.Hechos.Multimedia;
import Metamapa.business.Hechos.TipoMultimedia;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MultimediaDTO {
  private String tipoMultimedia;
  private String path;

  public Multimedia toDomain() {
    return new Multimedia(
            TipoMultimedia.valueOf(tipoMultimedia),
            path
    );
  }
}