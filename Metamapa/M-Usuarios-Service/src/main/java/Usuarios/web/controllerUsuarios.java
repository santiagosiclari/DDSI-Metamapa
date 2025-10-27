package Usuarios.web;
import Usuarios.business.Usuarios.*;
import java.util.*;
import java.util.stream.Collectors;
import Usuarios.DTO.UsuarioDTO;
import Usuarios.persistencia.RepositorioUsuarios;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class controllerUsuarios {
  private final RepositorioUsuarios usersRepository;

  public controllerUsuarios(RepositorioUsuarios usersRepository) {
    this.usersRepository = usersRepository;
  }

  @PostMapping(value = "/usuarios", consumes = "application/json", produces = "application/json")
  public ResponseEntity<UsuarioDTO> subirUsuario(@RequestBody Map<String, Object> requestBody) {
    try {
      String email = (String) requestBody.get("email");
      String contraseniaHasheada = (String) requestBody.get("contrasenia");
      String nombre = (String) requestBody.get("nombre");
      String apellido = (String) requestBody.get("apellido");
      Integer edad = (Integer) requestBody.get("edad");
      List<String> rolesInput = (List<String>) requestBody.get("roles");  // Recibe roles como lista de strings
      Set<Rol> roles = rolesInput.stream()
          .map(Rol::valueOf)  // Convierte el string a un Rol
          .collect(Collectors.toSet());
      Usuario user = new Usuario(email, contraseniaHasheada,nombre, apellido, edad,roles);
      System.out.println("User creado " + user);
      usersRepository.save(user);
      return ResponseEntity.ok(new UsuarioDTO(user));
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
/*
  @GetMapping(value = "/usuarios/{id}", produces = "application/json")
  public ResponseEntity<UsuarioDTO> getUsuario(@PathVariable("id") Integer id){
    try{
      Optional<Usuario> usuarioOpt = usersRepository.findById(id);
      return usuarioOpt.map(usuario -> ResponseEntity.ok(new UsuarioDTO(usuario)))
          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      System.err.println("Error al obtener el usuario: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
  */
}