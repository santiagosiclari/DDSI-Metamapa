package Usuarios.security;

import Usuarios.business.Usuarios.Usuario;
import Usuarios.persistencia.RepositorioUsuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UsuarioDetailsService implements UserDetailsService {

  private final RepositorioUsuarios usuarioRepo;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Usuario user = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    return User.builder()
            .username(user.getEmail())
            .password(user.getContraseniaHasheada())
            .roles(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
            .build();
  }
}