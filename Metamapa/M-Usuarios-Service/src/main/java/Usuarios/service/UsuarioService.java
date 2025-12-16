package Usuarios.service;

import Usuarios.business.Usuarios.Usuario;
import Usuarios.business.Usuarios.Rol;
import Usuarios.persistencia.RepositorioUsuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final RepositorioUsuarios usuarioRepo;
  private final PasswordEncoder passwordEncoder;

  public Usuario registrar(String email, String contrasenia, String nombre, String apellido, Integer edad, Set<Rol> roles) {
    if (usuarioRepo.existsByEmail(email)) {
      throw new RuntimeException("Ya existe un usuario con ese email.");
    }

    Usuario u = Usuario.builder()
            .email(email)
            .contraseniaHasheada(passwordEncoder.encode(contrasenia))
            .nombre(nombre)
            .apellido(apellido)
            .edad(edad)
            .roles(roles)
            .build();

    return usuarioRepo.save(u);
  }

  public Optional<Usuario> buscarPorEmail(String email) {
    return usuarioRepo.findByEmail(email);
  }

  public List<Usuario> listarUsuarios() {
    return usuarioRepo.findAll();
  }

  public Usuario sincronizarUsuarioSSO(String email, String nombreCompleto) {
    Optional<Usuario> usuarioExistente = usuarioRepo.findByEmail(email);

    if (usuarioExistente.isPresent()) {
      return usuarioExistente.get();
    } else {
      Usuario nuevoUsuario = Usuario.builder()
              .email(email)
              .contraseniaHasheada("{ssoregistrado}" + UUID.randomUUID().toString())
              .nombre(nombreCompleto)
              .apellido("")
              .roles(Set.of(Rol.VISUALIZADOR))
              .build();

      return usuarioRepo.save(nuevoUsuario);
    }
  }
}
