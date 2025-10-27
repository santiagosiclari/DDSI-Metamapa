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

  public Usuario registrar(String email, String contrasenia, String nombre, String apellido) {
    if (usuarioRepo.existsByEmail(email)) {
      throw new RuntimeException("Ya existe un usuario con ese email.");
    }

    Usuario u = Usuario.builder()
        .email(email)
        .contraseniaHasheada(passwordEncoder.encode(contrasenia))
        .nombre(nombre)
        .apellido(apellido)
        .roles(Set.of(Rol.VISUALIZADOR))
        .build();

    return usuarioRepo.save(u);
  }

  public Optional<Usuario> buscarPorEmail(String email) {
    return usuarioRepo.findByEmail(email);
  }

  public List<Usuario> listarUsuarios() {
    return usuarioRepo.findAll();
  }
}
