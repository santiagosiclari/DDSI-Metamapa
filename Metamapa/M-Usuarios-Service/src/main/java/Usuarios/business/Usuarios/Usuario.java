package Usuarios.business.Usuarios;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class Usuario {
  @Getter
  private String email;
  @Getter
  private String contraseniaHasheada;
  @Getter
  private List<Rol> roles;
  @Getter
  static public Integer contadorID = 1;
  @Getter
  public Integer id;
  @Getter
  private String nombre;
  @Getter
  private String apellido;
  @Getter
  private Integer edad;
  @Getter
  private List<Integer> solicitudesDeEliminacion;
  @Getter
  private List<Integer> solicitudesDeEdicion;

  public Usuario(String email, String contraseniaHasheada, String nombre, String apellido, Integer edad, List<Rol> roles) {
    this.email = email;
    this.contraseniaHasheada = contraseniaHasheada;
    this.nombre = nombre;
    this.apellido = apellido;
    this.edad = edad;
    this.solicitudesDeEliminacion = new ArrayList<>();
    this.solicitudesDeEdicion = new ArrayList<>();
    this.roles = roles;
  }

  public Boolean tieneRol(Rol rol) {
    return this.getRoles().contains(rol);
  }
}