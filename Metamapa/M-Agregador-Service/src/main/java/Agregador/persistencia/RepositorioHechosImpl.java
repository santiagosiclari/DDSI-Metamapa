package Agregador.persistencia;

import Agregador.DTO.FiltrosHechosDTO;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.Consenso;
import Agregador.business.Hechos.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;

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
  @Override
  public List<Criterio> construirCriterios(FiltrosHechosDTO filtros, boolean incluir) {
    List<Criterio> criterios = new ArrayList<>();
    if (incluir) {
      if (filtros.getTituloP() != null) criterios.add(new CriterioTitulo(filtros.getTituloP(), true));
      if (filtros.getDescripcionP() != null) criterios.add(new CriterioDescripcion(filtros.getDescripcionP(), true));
      if (filtros.getCategoriaP() != null) criterios.add(new CriterioCategoria(filtros.getCategoriaP(), true));
      if (filtros.getFechaReporteDesdeP() != null || filtros.getFechaReporteHastaP() != null)
        criterios.add(new CriterioFechaReportaje(filtros.getFechaReporteDesdeP(), filtros.getFechaReporteHastaP(), true));
      if (filtros.getFechaAcontecimientoDesdeP() != null || filtros.getFechaAcontecimientoHastaP() != null)
        criterios.add(new CriterioFecha(filtros.getFechaAcontecimientoDesdeP(), filtros.getFechaAcontecimientoHastaP(), true));
      if (filtros.getLatitudP() != null && filtros.getLongitudP() != null && filtros.getRadioP() != null)
        criterios.add(new CriterioUbicacion(filtros.getLatitudP(), filtros.getLongitudP(),filtros.getRadioP(), true));
      if (filtros.getTipoMultimediaP() != null) criterios.add(new CriterioMultimedia(TipoMultimedia.valueOf(filtros.getTipoMultimediaP()), true));
    } else {
      if (filtros.getTituloNP() != null) criterios.add(new CriterioTitulo(filtros.getTituloNP(), false));
      if (filtros.getDescripcionNP() != null) criterios.add(new CriterioDescripcion(filtros.getDescripcionNP(), false));
      if (filtros.getCategoriaNP() != null) criterios.add(new CriterioCategoria(filtros.getCategoriaNP(), false));
      if (filtros.getFechaReporteDesdeNP() != null || filtros.getFechaReporteHastaNP() != null)
        criterios.add(new CriterioFechaReportaje(filtros.getFechaReporteDesdeNP(), filtros.getFechaReporteHastaNP(), false));
      if (filtros.getFechaAcontecimientoDesdeNP() != null || filtros.getFechaAcontecimientoHastaNP() != null)
        criterios.add(new CriterioFecha(filtros.getFechaAcontecimientoDesdeNP(), filtros.getFechaAcontecimientoHastaNP(), false));
      if (filtros.getLatitudNP() != null && filtros.getLongitudNP() != null&& filtros.getRadioNP() != null)
        criterios.add(new CriterioUbicacion(filtros.getLatitudNP(), filtros.getLongitudNP(),filtros.getRadioNP(), false));
      if (filtros.getTipoMultimediaNP() != null) criterios.add(new CriterioMultimedia(TipoMultimedia.valueOf(filtros.getTipoMultimediaNP()), false));
    }
    return criterios;
  }

  @Override
  public List<Hecho> buscarPorTextoLibre(String textoBusqueda) {
    if (textoBusqueda == null || textoBusqueda.isBlank()) {
      return Collections.emptyList();
    }

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Hecho> query = cb.createQuery(Hecho.class);
    Root<Hecho> root = query.from(Hecho.class);

    // Convertir el texto a un patrón LIKE (ej: "%incendio%")
    String patronBusqueda = "%" + textoBusqueda.toLowerCase() + "%";

    // Crear la condición OR: (titulo LIKE %texto%) OR (descripcion LIKE %texto%)
    Predicate busquedaPredicate = cb.or(
            // Buscar en título (ignorando mayúsculas/minúsculas)
            cb.like(cb.lower(root.get("titulo")), patronBusqueda),
            // Buscar en descripción (ignorando mayúsculas/minúsculas)
            cb.like(cb.lower(root.get("descripcion")), patronBusqueda)
    );

    // Aplicar la condición a la consulta
    query.select(root).where(busquedaPredicate);

    return em.createQuery(query).getResultList();
  }
  @Override
  public Page<Hecho> findAll(Pageable pageable) {
    // 1. Crear el CriteriaBuilder
    CriteriaBuilder cb = em.getCriteriaBuilder();

    // 2. Consulta para obtener los Hechos (con LIMIT y OFFSET)
    CriteriaQuery<Hecho> query = cb.createQuery(Hecho.class);
    Root<Hecho> root = query.from(Hecho.class);
    query.select(root);

    TypedQuery<Hecho> typedQuery = em.createQuery(query);

    // Aplicar el OFFSET (setFirstResult) y el LIMIT (setMaxResults)
    typedQuery.setFirstResult((int) pageable.getOffset());
    typedQuery.setMaxResults(pageable.getPageSize());

    List<Hecho> content = typedQuery.getResultList();

    // 3. Consulta para obtener el TOTAL (Necesario para el objeto Page)
    // Se usa una consulta separada para contar todos los registros sin límite/offset
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    countQuery.select(cb.count(countQuery.from(Hecho.class)));
    Long total = em.createQuery(countQuery).getSingleResult();

    // 4. Devolver la página de Spring Data con el contenido limitado
    return new PageImpl<>(content, pageable, total);
  }
}