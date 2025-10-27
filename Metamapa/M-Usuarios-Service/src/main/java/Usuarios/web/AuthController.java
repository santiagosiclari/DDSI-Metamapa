package Usuarios.web;

import Usuarios.business.Usuarios.Usuario;
import Usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api-auth")
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
    if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
    return ResponseEntity.ok(Map.of("usuario", auth.getName()));
  }
}
