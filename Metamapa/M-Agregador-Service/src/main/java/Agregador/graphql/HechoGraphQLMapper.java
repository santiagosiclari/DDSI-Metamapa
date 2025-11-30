package Agregador.graphql;

import Agregador.business.Hechos.Hecho;

import java.time.format.DateTimeFormatter;

public class HechoGraphQLMapper {

  private static final DateTimeFormatter FORMATTER =
          DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public static HechoGraphQLDto toDto(Hecho h) {
    HechoGraphQLDto dto = new HechoGraphQLDto();
    dto.setId(h.getId());
    dto.setTitulo(h.getTitulo());
    dto.setDescripcion(h.getDescripcion());
    dto.setCategoria(h.getCategoria());
    dto.setLatitud(h.getLatitud());
    dto.setLongitud(h.getLongitud());
    dto.setAnonimo(h.getAnonimo());

    if (h.getFechaHecho() != null) {
      dto.setFechaHecho(h.getFechaHecho().format(FORMATTER));
    }

    return dto;
  }
}
