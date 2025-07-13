package DTO;
import domain.business.Usuarios.Perfil;
import domain.business.Usuarios.Rol;
import domain.business.Usuarios.Usuario;
import java.util.List;
import lombok.Getter;

@Getter
public class UsuarioDTO {
  private final String email;
  private final String contraseniaHasheada;
  private final Perfil perfil;
  private final List<Rol> roles;
  public UsuarioDTO(Usuario usuario) {
    this.email = usuario.getEmail();
    this.contraseniaHasheada = usuario.getContraseniaHasheada();
    this.perfil = usuario.getPerfil();
    this.roles = usuario.getRoles();
  }
}
