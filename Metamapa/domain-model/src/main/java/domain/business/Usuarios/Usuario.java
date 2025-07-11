package domain.business.Usuarios;
import java.util.List;
import lombok.Getter;

public class Usuario {
  @Getter
  private String email;
  @Getter
  private String contraseniaHasheada;
  @Getter
  private Perfil perfil;
  @Getter
  private List<Rol> roles;
  @Getter
  static public Integer contadorID = 1;
  @Getter
  public Integer id;

  public Usuario(String email, String contraseniaHasheada, Perfil perfil, List<Rol> roles) {
    this.email = email;
    this.contraseniaHasheada = contraseniaHasheada;
    this.perfil = perfil;
    this.roles = roles;
  }

  public Boolean tieneRol(Rol rol) {
    return this.getRoles().contains(rol);
  }
}