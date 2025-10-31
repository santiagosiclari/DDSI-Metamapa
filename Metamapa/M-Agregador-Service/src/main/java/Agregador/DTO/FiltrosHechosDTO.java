package Agregador.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Data
public class FiltrosHechosDTO {
  // --- Inclusión ---
  private String tituloP;
  private String descripcionP;
  private String categoriaP;
  private LocalDateTime fechaReporteDesdeP;
  private LocalDateTime fechaReporteHastaP;
  private LocalDateTime fechaAcontecimientoDesdeP;
  private LocalDateTime fechaAcontecimientoHastaP;
  private Float latitudP;
  private Float longitudP;
  private Float radioP;
  private String tipoMultimediaP;
  // --- Exclusión ---
  private String tituloNP;
  private String descripcionNP;
  private String categoriaNP;
  private LocalDateTime fechaReporteDesdeNP;
  private LocalDateTime fechaReporteHastaNP;
  private LocalDateTime fechaAcontecimientoDesdeNP;
  private LocalDateTime fechaAcontecimientoHastaNP;
  private Float latitudNP;
  private Float longitudNP;
  private Float radioNP;
  private String tipoMultimediaNP;
}