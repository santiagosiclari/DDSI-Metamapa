package Estadistica.DTO;
import java.util.Map;

public class EstadisticaDTO {
  // Categoría más reportada
  private String categoriaMasReportada;
  private long cantidadCategoriaMasReportada;

  // Provincia con más hechos (en general o por colección)
  private String provinciaMasReportada;
  private long cantidadProvinciaMasReportada;

  // Provincia con más hechos para una categoría concreta
  private String provinciaPorCategoria;
  private long cantidadProvinciaPorCategoria;

  // Distribución horaria de hechos para una categoría (ej: {"00":5,"01":2,...})
  private Map<Integer, Long> hechosPorHoraCategoria;

  // Cantidad de solicitudes de eliminación que son spam
  private long cantidadSolicitudesSpam;

  // Getters y Setters
  public String getCategoriaMasReportada() { return categoriaMasReportada; }
  public void setCategoriaMasReportada(String categoriaMasReportada) { this.categoriaMasReportada = categoriaMasReportada; }

  public long getCantidadCategoriaMasReportada() { return cantidadCategoriaMasReportada; }
  public void setCantidadCategoriaMasReportada(long cantidadCategoriaMasReportada) { this.cantidadCategoriaMasReportada = cantidadCategoriaMasReportada; }

  public String getProvinciaMasReportada() { return provinciaMasReportada; }
  public void setProvinciaMasReportada(String provinciaMasReportada) { this.provinciaMasReportada = provinciaMasReportada; }

  public long getCantidadProvinciaMasReportada() { return cantidadProvinciaMasReportada; }
  public void setCantidadProvinciaMasReportada(long cantidadProvinciaMasReportada) { this.cantidadProvinciaMasReportada = cantidadProvinciaMasReportada; }

  public String getProvinciaPorCategoria() { return provinciaPorCategoria; }
  public void setProvinciaPorCategoria(String provinciaPorCategoria) { this.provinciaPorCategoria = provinciaPorCategoria; }

  public long getCantidadProvinciaPorCategoria() { return cantidadProvinciaPorCategoria; }
  public void setCantidadProvinciaPorCategoria(long cantidadProvinciaPorCategoria) { this.cantidadProvinciaPorCategoria = cantidadProvinciaPorCategoria; }

  public Map<Integer, Long> getHechosPorHoraCategoria() { return hechosPorHoraCategoria; }
  public void setHechosPorHoraCategoria(Map<Integer, Long> hechosPorHoraCategoria) { this.hechosPorHoraCategoria = hechosPorHoraCategoria; }

  public long getCantidadSolicitudesSpam() { return cantidadSolicitudesSpam; }
  public void setCantidadSolicitudesSpam(long cantidadSolicitudesSpam) { this.cantidadSolicitudesSpam = cantidadSolicitudesSpam; }

}