package DTO;

import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import domain.business.incidencias.Ubicacion;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;

@Getter
public class HechoDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Ubicacion ubicacion;
  private LocalDate fechaHecho;
  private LocalDate fechaCarga;
  private LocalDate fechaModificacion;
  private Perfil perfil;
  private Integer fuenteId;
  private Boolean anonimo;
  private Boolean eliminado;
  private List<Multimedia> multimedia;
  private HashMap<String, String> metadata;
  private Integer id;
  public HechoDTO(Hecho hecho) {
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria();
    this.ubicacion = hecho.getUbicacion();
    this.fechaHecho = hecho.getFechaHecho();
    this.fechaCarga = hecho.getFechaCarga();
    this.fechaModificacion = hecho.getFechaModificacion();
    this.perfil = hecho.getPerfil();
    this.fuenteId = hecho.getFuenteId();
    this.anonimo = hecho.getAnonimo();
    this.eliminado = hecho.getEliminado();
    this.multimedia = hecho.getMultimedia();
    this.metadata = hecho.getMetadata();
    this.id = hecho.getId();
  }
}