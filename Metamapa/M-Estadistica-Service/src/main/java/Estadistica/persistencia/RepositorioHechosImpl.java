package Estadistica.persistencia;

import Estadistica.business.Estadistica.Criterios.Criterio;
import Estadistica.business.Estadistica.Hecho;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class RepositorioHechosImpl implements RepositorioHechosCustom {
  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Hecho> filtrarPorCriterios(List<Criterio> criterios) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Hecho> query = cb.createQuery(Hecho.class);
    Root<Hecho> root = query.from(Hecho.class);
    List<Predicate> predicates = new ArrayList<>();
    criterios.forEach(c -> predicates.add(c.toPredicate(root, cb)));
    query.select(root)
        .where(cb.and(predicates.toArray(new Predicate[0])));

    List<Hecho> filtrados = em.createQuery(query).getResultList();
    // aplicar criterios no SQL (como CriterioFuenteDeDatos) en memoria
    for (Criterio c : criterios) {
      filtrados = filtrados.stream()
              .filter(c::cumple)
              .toList();
    }
    return filtrados;
  }
}