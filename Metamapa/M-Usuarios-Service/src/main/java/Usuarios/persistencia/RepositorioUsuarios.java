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
public interface RepositorioUsuarios extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByEmail (String email);
  boolean existsByEmail (String email);
}
//
//  private final List<Usuario> usuarios = new ArrayList<>();  // Lista en memoria para almacenar los usuarios
//
//  // Guardar un nuevo usuario
//  public void save(Usuario usuario) {
//    usuarios.add(usuario);  // Agrega el usuario a la lista
//  }
//
//  // Buscar un usuario por su ID (o email en este caso)
//  public Optional<Usuario> findByEmail(String email) {
//    return usuarios.stream()
//        .filter(usuario -> usuario.getEmail().equals(email))
//        .findFirst();  // Devuelve el primer usuario que coincide con el email
//  }
//  public Optional<Usuario> findById(Integer id) {
//    return usuarios.stream()
//        .filter(usuario -> usuario.getId().equals(id))
//        .findFirst();  // Devuelve el primer usuario que coincide con el email
//  }
//
//  // Obtener todos los usuarios
//  public List<Usuario> findAll() {
//    return new ArrayList<>(usuarios);  // Devuelve una copia de la lista de usuarios
//  }
//
//  // Eliminar un usuario por su email
//  public boolean deleteByEmail(String email) {
//    return usuarios.removeIf(usuario -> usuario.getEmail().equals(email));
//  }
//
//  // Actualizar los roles de un usuario por email
//  /*public boolean updateRoles(String email, List<Rol> roles) {
//    Optional<Usuario> usuarioOpt = findByEmail(email);
//    if (usuarioOpt.isPresent()) {
//      Usuario usuario = usuarioOpt.get();
//      usuario.setRoles(roles);  // Actualiza los roles del usuario
//      return true;
//    }
//    return false;
//  }*/