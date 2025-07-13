package domain.business.incidencias;
import lombok.Getter;
import lombok.Setter;

public class Multimedia {
  @Getter @Setter
  public TipoMultimedia tipoMultimedia;
  @Getter @Setter
  String path;
  public Multimedia(){}

  public Multimedia(TipoMultimedia tipoMultimedia, String path) {
    this.tipoMultimedia = tipoMultimedia;
    this.path = path;
  }

}