package Agregador.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @Data
public class FiltrosHechosDTO {
  // --- Inclusión ---
  private String tituloP;
  private String descripcionP;
  private String categoriaP;
  private LocalDate fechaReporteDesdeP;
  private LocalDate fechaReporteHastaP;
  private LocalDate fechaAcontecimientoDesdeP;
  private LocalDate fechaAcontecimientoHastaP;
  private Float latitudP;
  private Float longitudP;
  private Integer radioP;
  private String tipoMultimediaP;
  // --- Exclusión ---
  private String tituloNP;
  private String descripcionNP;
  private String categoriaNP;
  private LocalDate fechaReporteDesdeNP;
  private LocalDate fechaReporteHastaNP;
  private LocalDate fechaAcontecimientoDesdeNP;
  private LocalDate fechaAcontecimientoHastaNP;
  private Float latitudNP;
  private Float longitudNP;
  private Integer radioNP;
  private String tipoMultimediaNP;
}