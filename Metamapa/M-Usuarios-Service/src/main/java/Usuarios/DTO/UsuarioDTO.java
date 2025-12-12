package Usuarios.DTO;

import Usuarios.business.Usuarios.Rol;
import Usuarios.business.Usuarios.Usuario;
import lombok.Getter;
import java.util.*;

@Getter
public class UsuarioDTO {
  private String email;
  private String contraseniaHasheada;
  private List<Rol> roles;
  private String nombre;
  private String apellido;
  private Integer edad;
  public UsuarioDTO(Usuario usuario) {
    this.email = usuario.getEmail();
    this.contraseniaHasheada = usuario.getContraseniaHasheada();
    this.nombre = usuario.getNombre();
    this.apellido = usuario.getApellido();
    this.edad = usuario.getEdad();
    this.roles = new ArrayList<>(usuario.getRoles());
  }
}