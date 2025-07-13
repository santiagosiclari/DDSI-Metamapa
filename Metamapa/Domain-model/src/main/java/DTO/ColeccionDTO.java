package DTO;

import domain.business.criterio.Coleccion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;


@Getter
public class ColeccionDTO {
  private final String titulo;
  private final String descripcion;
  private final UUID handle;
  private final String consenso;
  private final List<CriterioDTO> criteriosPertenencia;
  private final List<CriterioDTO> criteriosNoPertenencia;
  public ColeccionDTO(Coleccion coleccion) {
    this.titulo = coleccion.getTitulo();
    this.descripcion = coleccion.getDescripcion();
    this.handle = coleccion.getHandle();
    this.consenso = coleccion.getConsenso() != null ? coleccion.getConsenso().getClass().getSimpleName() : null;
    this.criteriosPertenencia = Optional.ofNullable(coleccion.getCriterioPertenencia())
        .orElse(new ArrayList<>()).stream()
        .map(CriterioDTO::new)
        .collect(Collectors.toList());
    this.criteriosNoPertenencia = Optional.ofNullable(coleccion.getCriterioNoPertenencia())
        .orElse(new ArrayList<>()).stream()
        .map(CriterioDTO::new)
        .collect(Collectors.toList());
  }
}