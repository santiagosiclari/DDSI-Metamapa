package DTO;

import domain.business.Agregador.Agregador;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;


public class AgregadorDTO {
  private static AgregadorDTO agregadorDTO= null;

  @Getter @Setter
  public ArrayList<FuenteDeDatosDTO> fuentesDeDatos;
  @Getter @Setter
  public ArrayList<HechoDTO> listaDeHechos;


  private AgregadorDTO() {
    this.fuentesDeDatos= new ArrayList<>();
    this.listaDeHechos= new ArrayList<>();
  }

  public static AgregadorDTO getInstance() {
    if (agregadorDTO == null)
      agregadorDTO = new AgregadorDTO();
    return agregadorDTO;
  }
}