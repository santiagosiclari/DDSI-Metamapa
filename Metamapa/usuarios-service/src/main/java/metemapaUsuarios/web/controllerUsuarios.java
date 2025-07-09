package metemapaUsuarios.web;
import domain.business.Usuarios.Perfil;
import domain.business.Usuarios.Rol;
import domain.business.Usuarios.Usuario;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import DTO.UsuarioDTO;
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


}
