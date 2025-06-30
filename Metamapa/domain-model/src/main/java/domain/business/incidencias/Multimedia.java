package domain.business.incidencias;
import lombok.Getter;

public class Multimedia {
  @Getter
  public TipoMultimedia tipoMultimedia;
  @Getter
  String path;
  public Multimedia(TipoMultimedia tipoMultimedia, String path) {
    this.tipoMultimedia = tipoMultimedia;
    this.path = path;
  }

}