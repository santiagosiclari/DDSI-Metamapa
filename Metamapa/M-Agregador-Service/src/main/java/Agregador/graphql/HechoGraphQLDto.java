package Agregador.graphql;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter@Setter
public class HechoGraphQLDto {

  private BigInteger id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private String fechaHecho;
  private Boolean anonimo;

}
