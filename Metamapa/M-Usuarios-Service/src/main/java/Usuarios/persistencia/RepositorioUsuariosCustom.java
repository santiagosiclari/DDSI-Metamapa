package Usuarios.persistencia;

import Usuarios.business.Usuarios.Usuario;
import java.util.Optional;

public interface RepositorioUsuariosCustom {
  Optional<Usuario> findByEmail (String email);
  boolean existsByEmail (String email);
}
