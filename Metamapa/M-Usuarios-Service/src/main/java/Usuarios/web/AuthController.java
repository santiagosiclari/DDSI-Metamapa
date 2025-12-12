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
@RequestMapping("/usuarios/api-auth")
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

    // 1. Verificar si es un usuario SSO (OidcUser)
    if (principal instanceof OidcUser oidcUser) {
      // Para OIDC/SSO, el email viene en los claims del token
      email = oidcUser.getClaims().get("email").toString();
    }
    // 2. Verificar si es un usuario Tradicional (UserDetails/Spring User)
    else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
      // Para el login tradicional, el email está en el nombre principal
      email = auth.getName();
    }
    // 3. Fallback (Si es otro tipo de principal, como un String simple)
    else {
      // Esto cubre casos simples o donde el Principal es solo el email
      email = auth.getName();
    }

    // Si no logramos obtener un email, fallamos la autenticación
    if (email == null) {
      return ResponseEntity.status(401).body(Map.of("error", "Email no encontrado en el contexto de seguridad"));
    }

    try{
      // Buscar el usuario de tu dominio usando el email
      Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);

      if (usuario.isPresent()) {
        // Éxito: Retorna ResponseEntity<Usuario>
        return ResponseEntity.ok(usuario.get());
      } else {
        // Falla: Retorna ResponseEntity<Map<String, String>>
        return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado en la BD local"));
      }

    } catch (Exception e){
      // Si hay un error de base de datos o servicio
      return ResponseEntity.status(500).body(Map.of("error", "Error al buscar el usuario: " + e.getMessage()));
    }
  }


}
