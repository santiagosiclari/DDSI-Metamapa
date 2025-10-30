package Usuarios.persistencia;

import Usuarios.business.Usuarios.Usuario;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import Usuarios.business.Usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepositorioUsuarios extends JpaRepository<Usuario, Long>, RepositorioUsuariosCustom{

}
