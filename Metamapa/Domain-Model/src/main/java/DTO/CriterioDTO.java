package DTO;

import domain.business.criterio.Criterio;
import domain.business.criterio.CriterioCategoria;
import domain.business.criterio.CriterioDescripcion;
import domain.business.criterio.CriterioFechaHecho;
import domain.business.criterio.CriterioFechaReportaje;
import domain.business.criterio.CriterioMultimedia;
import domain.business.criterio.CriterioTitulo;
import domain.business.criterio.CriterioUbicacion;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class CriterioDTO {
  private final String tipo;
  private final Map<String, Object> parametros;
  public CriterioDTO(Criterio criterio) {
    this.tipo = criterio.getClass().getSimpleName();
    this.parametros = new HashMap<>();
    // Extraer parámetros específicos según el tipo de criterio
    if (criterio instanceof CriterioTitulo ct) {
      parametros.put("titulo", ct.getTitulo());
    } else if (criterio instanceof CriterioDescripcion cd) {
      parametros.put("descripcion", cd.getDescripcion());
    } else if (criterio instanceof CriterioCategoria cc) {
      parametros.put("categoria", cc.getCategoria());
    } else if (criterio instanceof CriterioFechaHecho cf) {
      parametros.put("fechaDesde", cf.getFechaDesde());
      parametros.put("fechaHasta", cf.getFechaHasta());
    } else if (criterio instanceof CriterioFechaReportaje cfr) {
      parametros.put("fechaDesde", cfr.getDesde());
      parametros.put("fechaHasta", cfr.getHasta());
    } else if (criterio instanceof CriterioUbicacion cu) {
      parametros.put("latitud", cu.getLatitud());
      parametros.put("longitud", cu.getLongitud());
    } else if (criterio instanceof CriterioMultimedia cm) {
      parametros.put("tipoMultimedia", cm.getTipoMultimedia().name());
    }
  }
}