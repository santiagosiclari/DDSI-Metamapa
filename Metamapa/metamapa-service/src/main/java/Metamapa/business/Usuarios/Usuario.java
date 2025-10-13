package Metamapa.business.Usuarios;
import java.util.*;
import lombok.Getter;

@Getter
public class Usuario {
  private String email;
  private String contraseniaHasheada;
  private List<Rol> roles;
  static public Integer contadorID = 1;
  public Integer id;
  private String nombre;
  private String apellido;
  private Integer edad;
  private List<Integer> solicitudesDeEliminacion;
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