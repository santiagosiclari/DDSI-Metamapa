package Estadistica.persistencia;

import Estadistica.business.Colecciones.*;
import Estadistica.business.Hechos.Hecho;
import jakarta.persistence.*;
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
    // Filtrar solo no eliminados
    predicates.add(cb.isFalse(root.get("eliminado")));
    // Agrego los predicados SQL
    criterios.forEach(c -> predicates.add(c.toPredicate(root, cb)));
    query.select(root)
            .where(cb.and(predicates.toArray(new Predicate[0])));
    List<Hecho> filtrados = em.createQuery(query).getResultList();
    // AGRUPAR CRITERIOS NO-SQL
    List<CriterioFuenteDeDatos> criteriosFuente = new ArrayList<>();
    List<Criterio> otrosNoSQL = new ArrayList<>();

    for (Criterio c : criterios) {
      if (c instanceof CriterioFuenteDeDatos f)
        criteriosFuente.add(f);
      else
        otrosNoSQL.add(c);
    }

    for (Criterio c : otrosNoSQL) {
      filtrados = filtrados.stream()
              .filter(c::cumple)        // AND
              .toList();
    }

    if (!criteriosFuente.isEmpty()) {
      filtrados = filtrados.stream()
              .filter(h -> {
                for (CriterioFuenteDeDatos cf : criteriosFuente) {
                  if (cf.cumple(h)) {
                    return true;
                  }
                }
                return false;
              })
              .toList();
    }
    return filtrados;
  }

@Override
public Optional<String> obtenerHoraConMasHechos(String categoria) {
  String sql = "SELECT TOP 1 DATEPART(HOUR, fecha_carga) AS hora, COUNT(*) AS cantidad " +
          "FROM hecho ";

  if (categoria != null && !categoria.isBlank()) {
    sql += "WHERE categoria = :categoria ";
  }

  sql += "GROUP BY DATEPART(HOUR, fecha_carga) " +
          "ORDER BY cantidad DESC";

  Query query = em.createNativeQuery(sql);
  if (categoria != null && !categoria.isBlank()) {
    query.setParameter("categoria", categoria);
  }

  List<Object[]> resultados = query.getResultList();
  if (resultados.isEmpty()) {
    return Optional.empty();
  }

  Integer hora = ((Number) resultados.get(0)[0]).intValue();
  return Optional.of(hora.toString());
}

  @Override
  public Optional<String> obtenerCategoriaConMasHechos() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<Hecho> root = query.from(Hecho.class);

    Expression<String> categoriaExpr = root.get("categoria");
    Expression<Long> countExpr = cb.count(root);

    query.multiselect(
                    categoriaExpr.alias("categoria"),
                    countExpr.alias("cantidad")
            )
            .groupBy(categoriaExpr)
            .orderBy(cb.desc(countExpr));

    List<Tuple> resultados = em.createQuery(query)
            .setMaxResults(1)
            .getResultList();

    if (resultados.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(
            resultados.get(0).get("categoria", String.class)
    );
  }

  @Override
  public List<String> obtenerCategorias() {
    return em.createQuery("SELECT DISTINCT h.categoria FROM Hecho h", String.class)
            .getResultList();
  }

  @Override
  public Optional<String> obtenerProvinciaConMasHechosPorCategoria(String categoria) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<Hecho> root = query.from(Hecho.class);

    Expression<String> provinciaExpr = root.get("provincia");
    Expression<Long> countExpr = cb.count(root);

    List<Predicate> predicates = new ArrayList<>();

    if (categoria != null && !categoria.isBlank()) {
      predicates.add(cb.equal(root.get("categoria"), categoria));
    }

    query.multiselect(
                    provinciaExpr.alias("provincia"),
                    countExpr.alias("cantidad")
            )
            .where(predicates.toArray(new Predicate[0]))
            .groupBy(provinciaExpr)
            .orderBy(cb.desc(countExpr));

    List<Tuple> resultados = em.createQuery(query)
            .setMaxResults(1)
            .getResultList();

    if (resultados.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(
            resultados.get(0).get("provincia", String.class)
    );
  }
}