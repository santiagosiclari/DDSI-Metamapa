package Agregador.persistencia;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Consenso.Consenso;
import Agregador.business.Hechos.Hecho;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioHechosImpl implements RepositorioHechosCustom {
  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Hecho> filtrarPorCriterios(List<Criterio> criterios, Consenso consenso) {
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
    if (consenso != null) {
      filtrados = filtrados.stream()
          .filter(h -> h.estaConsensuado(consenso))
          .toList();
    }
    return filtrados;
  }
}