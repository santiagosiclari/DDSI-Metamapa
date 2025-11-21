package Usuarios.web;

import Usuarios.business.Usuarios.Usuario;
import Usuarios.service.UsuarioService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/usuarios/api-auth")
@RequiredArgsConstructor
public class AuthController {

  private final UsuarioService usuarioService;
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
    Usuario nuevo = usuarioService.registrar(
        body.get("email"),
        body.get("password"),
        body.get("nombre"),
        body.get("apellido")
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
