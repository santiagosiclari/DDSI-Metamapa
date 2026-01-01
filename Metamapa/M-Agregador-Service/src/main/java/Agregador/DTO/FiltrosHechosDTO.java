package Agregador.DTO;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter @Setter @Data
public class FiltrosHechosDTO {
  // --- Inclusión ---
  private String tituloP;
  private String descripcionP;
  private String categoriaP;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaReporteDesdeP;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaReporteHastaP;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaAcontecimientoDesdeP;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaAcontecimientoHastaP;

  private Float latitudP;
  private Float longitudP;
  private Float radioP;
  private String tipoMultimediaP;
  // --- Exclusión ---
  private String tituloNP;
  private String descripcionNP;
  private String categoriaNP;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaReporteDesdeNP;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaReporteHastaNP;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaAcontecimientoDesdeNP;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaAcontecimientoHastaNP;
  private Float latitudNP;
  private Float longitudNP;
  private Float radioNP;
  private String tipoMultimediaNP;
}