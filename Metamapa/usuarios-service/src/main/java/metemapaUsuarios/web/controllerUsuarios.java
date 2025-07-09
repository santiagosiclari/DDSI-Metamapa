package metemapaUsuarios.web;

import domain.business.Usuarios.Perfil;
import domain.business.Usuarios.Rol;
import domain.business.Usuarios.Usuario;
import domain.business.tiposSolicitudes.SolicitudEdicion;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import metemapaUsuarios.persistencia.RepositorioUsuarios;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class controllerUsuarios {
  private final RepositorioUsuarios usersRepository = new RepositorioUsuarios();

  @PostMapping(value = "/usuarios", consumes = "application/json", produces = "application/json")
  public ResponseEntity<UsuarioDTO> subirUsuario(@RequestBody Map<String, Object> requestBody) {
    try {
      String email = (String) requestBody.get("email");
      String contraseniaHasheada = (String) requestBody.get("contrasenia");
      String nombre = (String) requestBody.get("nombre");
      String apellido = (String) requestBody.get("apellido");
      Integer edad = (Integer) requestBody.get("edad");
      Perfil perfil = new Perfil(nombre,apellido,edad);
      List<String> rolesInput = (List<String>) requestBody.get("roles");  // Recibe roles como lista de strings
      List<Rol> roles = rolesInput.stream()
          .map(Rol::valueOf)  // Convierte el string a un Rol
          .collect(Collectors.toList());

      Usuario user = new Usuario(email, contraseniaHasheada,perfil,roles);
      System.out.println("User creadao " + user);
      usersRepository.save(user);
      return ResponseEntity.ok(new UsuarioDTO(user));
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  @Getter
  public static class UsuarioDTO {
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

  @Getter
  public static class PerfilDTO {
    private final String nombre;
    private final String apellido;
    private final Integer edad;
    private final List<SolicitudEliminacion> solicitudesDeEliminacion;
    private final List<SolicitudEdicion> solicitudesDeEdicion;

    public PerfilDTO(Perfil perfil) {
      this.nombre = perfil.getNombre();
      this.apellido = perfil.getApellido();
      this.edad = perfil.getEdad();
      this.solicitudesDeEliminacion = new ArrayList<>();
      this.solicitudesDeEdicion = new ArrayList<>();
    }
  }
}
