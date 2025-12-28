package Usuarios.persistencia;

import Usuarios.business.Usuarios.Usuario;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.*;

@Repository
public class RepositorioUsuariosImpl implements RepositorioUsuariosCustom{
  @PersistenceContext
  private EntityManager em;

  @Transactional(readOnly = true)
  public Optional<Usuario> findByEmail (String email)
  {
    try {
      Usuario usuario = em.createQuery("""
                SELECT DISTINCT u
                FROM Usuario u
                WHERE u.email = :email
            """, Usuario.class)
              .setParameter("email", email)
              .getSingleResult();
      return Optional.of(usuario);
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
  public boolean existsByEmail (String email)
  {
    return findByEmail(email).isPresent();
  }
}