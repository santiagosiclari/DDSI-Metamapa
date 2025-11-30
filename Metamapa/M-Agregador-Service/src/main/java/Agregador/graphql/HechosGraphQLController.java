package Agregador.graphql;

import Agregador.DTO.FiltrosHechosDTO;
import Agregador.DTO.HechoDTO;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.RepositorioHechos;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HechosGraphQLController {

  private final RepositorioHechos repositorioHechos;

  public HechosGraphQLController(RepositorioHechos repositorioHechos) {
    this.repositorioHechos = repositorioHechos;
  }

  @QueryMapping
  public List<HechoDTO> hechos(
          @Argument String titulo,
          @Argument String descripcion,
          @Argument String categoria,
          @Argument String fechaDesde,
          @Argument String fechaHasta
  ) {

    FiltrosHechosDTO filtros = new FiltrosHechosDTO();
    filtros.setTituloP(titulo);
    filtros.setDescripcionP(descripcion);
    filtros.setCategoriaP(categoria);

    if (fechaDesde != null && !fechaDesde.isBlank()) {
      filtros.setFechaAcontecimientoDesdeP(parseFecha(fechaDesde));
    }
    if (fechaHasta != null && !fechaHasta.isBlank()) {
      filtros.setFechaAcontecimientoHastaP(parseFecha(fechaHasta));
    }

    List<Criterio> criterios = new ArrayList<>();
    criterios.addAll(repositorioHechos.construirCriterios(filtros, true));
    criterios.addAll(repositorioHechos.construirCriterios(filtros, false));

    List<Hecho> hechos;
    if (criterios.isEmpty()) {
      hechos = repositorioHechos.findAll();
    } else {
      hechos = repositorioHechos.filtrarPorCriterios(criterios, null);
    }

    return hechos.stream()
            .map(HechoDTO::new)
            .toList();
  }

  private LocalDateTime parseFecha(String valor) {
    try {
      return LocalDateTime.parse(valor);
    } catch (DateTimeParseException e) {
      return null;
    }
  }
}
