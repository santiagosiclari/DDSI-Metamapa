package Usuarios.web;

import Usuarios.business.Usuarios.*;
import Usuarios.service.UsuarioService;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-auth")
@RequiredArgsConstructor
public class AuthController {
  private final UsuarioService usuarioService;

  @PostMapping("/registrar")
  public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
    List<String> rolesList = (List<String>) body.get("roles"); // <-- ahora sí
    Set<Rol> roles = rolesList.stream()
            .map(String::trim)
            .map(Rol::valueOf)
            .collect(Collectors.toSet());
    Usuario nuevo = usuarioService.registrar(
            (String) body.get("email"),
            (String) body.get("password"),
            (String) body.get("nombre"),
            (String) body.get("apellido"),
            (Integer) body.get("edad"),
            roles
    );
    return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado con éxito", "id", nuevo.getId()));
  }

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
    }
    String email;
    Object principal = auth.getPrincipal();

    if (principal instanceof OidcUser oidcUser) {
      email = oidcUser.getClaims().get("email").toString();
    }
    else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
      email = auth.getName();
    }
    else {
      email = auth.getName();
    }

    if (email == null) {
      return ResponseEntity.status(401).body(Map.of("error", "Email no encontrado en el contexto de seguridad"));
    }

    try{
      Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);

      if (usuario.isPresent()) {
        return ResponseEntity.ok(usuario.get());
      } else {
        return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado en la BD local"));
      }

    } catch (Exception e){
      return ResponseEntity.status(500).body(Map.of("error", "Error al buscar el usuario: " + e.getMessage()));
    }
  }
}