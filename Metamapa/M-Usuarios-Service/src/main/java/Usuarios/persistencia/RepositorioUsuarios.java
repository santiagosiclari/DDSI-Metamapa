package Usuarios.persistencia;

import Usuarios.business.Usuarios.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioUsuarios extends JpaRepository<Usuario, Long>, RepositorioUsuariosCustom{

}
