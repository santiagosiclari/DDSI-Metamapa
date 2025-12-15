package Agregador.persistencia;

import Agregador.business.Colecciones.*;
import jakarta.persistence.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioColeccionesImpl implements RepositorioColeccionesCustom {
  @PersistenceContext
  private EntityManager em;

  @Override
  public Optional<Coleccion> getColeccion(UUID id) {
    try {
      Coleccion coleccion = em.createQuery("""
                          SELECT DISTINCT c
                          FROM Coleccion c
                          LEFT JOIN FETCH c.criterios
                          LEFT JOIN FETCH c.consenso
                          WHERE c.handle = :id
                      """, Coleccion.class)
              .setParameter("id", id)
              .getSingleResult();
      return Optional.of(coleccion);
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Criterio> getCriteriosColeccion(UUID id) {
    Coleccion coleccion = em.createQuery("""
                        SELECT c FROM Coleccion c 
                        LEFT JOIN FETCH c.criterios 
                        WHERE c.handle = :id
                    """, Coleccion.class)
            .setParameter("id", id)
            .getSingleResult();
    List<Criterio> criterios = new ArrayList<>();
    criterios.addAll(coleccion.getCriterios());
    return criterios;
  }

  @Override
  public List<Coleccion> buscarTextoLibre(String query) {
    return em.createQuery("""
          SELECT c
          FROM Coleccion c
          WHERE LOWER(c.titulo) LIKE CONCAT(CONCAT('%', :q), '%')
             OR LOWER(c.descripcion) LIKE CONCAT(CONCAT('%', :q), '%')
      """, Coleccion.class)
            .setParameter("q", query.toLowerCase(java.util.Locale.ROOT))
            .getResultList();
  }
}