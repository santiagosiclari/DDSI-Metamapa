package Metamapa.business.FuentesDeDatos;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TipoFuente {
  FUENTEDEMO,
  FUENTEDINAMICA,
  FUENTEESTATICA,
  FUENTEMETAMAPA,
  FUENTEPROXY
}