package DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import domain.business.FuentesDeDatos.FuenteDemo;
import domain.business.FuentesDeDatos.FuenteProxy;
import domain.business.FuentesDeDatos.TipoFuente;
import domain.business.externo.demo.Conexion;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;


@JsonTypeName("FUENTEDEMO")
public class FuenteDemoDTO extends FuenteProxyDTO {
  @Getter
  private LocalDateTime fechaUltimaConsulta;

  public FuenteDemoDTO(){}
  public FuenteDemoDTO(String nombreFuente, String endpointBase,ArrayList<HechoDTO> hechos,LocalDateTime fechaUltimaConsulta,Integer id) {
    this.nombre = nombreFuente;
    this.endpointBase = endpointBase;
    this.hechos = hechos;
    this.fechaUltimaConsulta = fechaUltimaConsulta;
    this.id = id;
    this.tipoFuente = TipoFuente.FUENTEDEMO;
  }
/*
  public static FuenteDemoDTO fromEntity(FuenteDemo fuente) {
    FuenteDemoDTO dto = new FuenteDemoDTO(fuente.getNombre(),fuente.getEndpointBase(),fuente.getHechos(),fuente.getFechaUltimaConsulta(),fuente.getId());
    return dto;
  }
  */
}
