package Usuarios.web;
import Usuarios.business.Usuarios.*;
import java.util.*;
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

  @GetMapping(value = "/detalle/{id}", produces = "application/json")
  public ResponseEntity<UsuarioDTO> getUsuario(@PathVariable("id") Long id){
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
}