package Agregador.DTO;
import Agregador.business.Colecciones.*;
import Agregador.business.Hechos.TipoMultimedia;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class CriterioDTO {
  @NotEmpty @NotBlank @NotNull
  private String tipo;
  private String valor;
  private String fechaDesde;
  private String fechaHasta;
  private Integer idFuenteDeDatos;
  private Float latitud;
  private Float longitud;
  private String tipoMultimedia;
  private Boolean inclusion;

  public CriterioDTO() {}

  public Criterio toDomain() {
    return switch (tipo.toLowerCase()) {
      case "titulo" -> new CriterioTitulo(valor,this.inclusion);
      case "descripcion" -> new CriterioDescripcion(valor,this.inclusion);
      case "categoria" -> new CriterioCategoria(valor,this.inclusion);
      case "fecha" -> new CriterioFecha(
              LocalDate.parse(fechaDesde),
              LocalDate.parse(fechaHasta),
              this.inclusion
      );
      case "fechareportaje" -> new CriterioFechaReportaje(
              LocalDate.parse(fechaDesde),
              LocalDate.parse(fechaHasta),
          this.inclusion
      );
      case "fuente", "criteriofuentededatos" -> new CriterioFuenteDeDatos(idFuenteDeDatos,this.inclusion); // << alias OK
      case "ubicacion" -> new CriterioUbicacion(latitud, longitud,this.inclusion);
      case "multimedia" -> new CriterioMultimedia(TipoMultimedia.valueOf(tipoMultimedia),this.inclusion);
      default -> throw new IllegalArgumentException("Tipo de criterio desconocido: " + tipo);
    };
  }

  public CriterioDTO(Criterio criterio) {
    this.tipo = criterio.getClass().getSimpleName().toLowerCase();
    System.out.println("INCLUSION!!!!!!!!!!!!!!!!!: " + criterio.getInclusion());
    this.inclusion = criterio.getInclusion();
    if (criterio instanceof CriterioTitulo ct) {
      this.valor = ct.getTitulo();
    } else if (criterio instanceof CriterioDescripcion cd) {
      this.valor = cd.getDescripcion();
    } else if (criterio instanceof CriterioCategoria cc) {
      this.valor = cc.getCategoria();
    } else if (criterio instanceof CriterioFecha cf) {
      this.fechaDesde = cf.getFechaDesde().toString();
      this.fechaHasta = cf.getFechaHasta().toString();
    } else if (criterio instanceof CriterioFechaReportaje cfr) {
      this.fechaDesde = cfr.getDesde().toString();
      this.fechaHasta = cfr.getHasta().toString();
    } else if (criterio instanceof CriterioFuenteDeDatos cfd) {
      this.tipo = "fuente"; // << corto y claro
      this.idFuenteDeDatos = cfd.getIdFuenteDeDatos();
    } else if (criterio instanceof CriterioUbicacion cu) {
      this.latitud = cu.getLatitud();
      this.longitud = cu.getLongitud();
    } else if (criterio instanceof CriterioMultimedia cm) {
      this.tipoMultimedia = cm.getTipoMultimedia().name();
    }
  }
}