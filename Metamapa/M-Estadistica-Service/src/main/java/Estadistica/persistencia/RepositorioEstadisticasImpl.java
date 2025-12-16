package Estadistica.persistencia;

import Estadistica.business.Colecciones.Coleccion;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;
import Estadistica.business.Estadistica.Estadistica;

public class RepositorioEstadisticasImpl implements RepositorioEstadisticasCustom {
  @PersistenceContext
  private EntityManager em;

  @Override
  public <T extends Estadistica> Optional<T> obtenerMasNueva(
          Class<T> clazz,
          Map<String, Object> filtros
  ) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> query = cb.createQuery(clazz);
    Root<T> root = query.from(clazz);

    List<Predicate> predicates = new ArrayList<>();

    if (filtros != null) {
      filtros.forEach((campo, valor) -> {
        if (valor == null) return;

        Path<?> path = root.get(campo);
        if (valor instanceof Coleccion c) {
          predicates.add(
                  cb.equal(path.get("handle"), c.getHandle())
          );
        } else {
          predicates.add(cb.equal(path, valor));
        }
      });
    }

    query.select(root)
            .where(predicates.toArray(new Predicate[0]))
            .orderBy(cb.desc(root.get("fechaDeMedicion")));

    return em.createQuery(query)
            .setMaxResults(1)
            .getResultStream()
            .findFirst();
  }
}
