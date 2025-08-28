package Agregador.business.Hechos;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Multimedia {
  public TipoMultimedia tipoMultimedia;
  public String path;

  public Multimedia(){}

  public Multimedia(TipoMultimedia tipoMultimedia, String path) {
    this.tipoMultimedia = tipoMultimedia;
    this.path = path;
  }

}