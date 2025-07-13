package DTO;

import com.fasterxml.jackson.annotation.JsonTypeName;

import domain.business.Parsers.HechoParser;
import lombok.Getter;
import lombok.Setter;


@JsonTypeName("FUENTEPROXY")
public abstract class FuenteProxyDTO extends FuenteDeDatosDTO {
  @Getter
  public String endpointBase;
  @Getter @Setter
  public HechoParser parser;

  public FuenteProxyDTO(String nombre,String endpointBase, Integer id) {
    this.nombre = nombre;
    this.endpointBase = endpointBase;
    this.id = id;
    this.tipoFuente = tipoFuente.FUENTEPROXY;
  }
  public FuenteProxyDTO() {}

}