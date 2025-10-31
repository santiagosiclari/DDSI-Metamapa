package Agregador.DTO;

import Agregador.business.Hechos.Hecho;
import Agregador.business.Hechos.Multimedia;
import Agregador.business.Consenso.Consenso;
import lombok.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HechoDTO {

  private BigInteger id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDateTime fechaHecho;
  private LocalDateTime fechaCarga;
  private LocalDateTime fechaModificacion;
  private String perfil; // solo nombre del usuario o null
  private Boolean anonimo;
  private Boolean eliminado;
  private Integer idFuente;
  private List<MultimediaDTO> multimedia;
  private List<ConsensoDTO> consensos;
  private Map<String, String> metadata;

  public HechoDTO(Hecho hecho) {
    this.id = hecho.getId();
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria();
    this.latitud = hecho.getLatitud();
    this.longitud = hecho.getLongitud();
    this.fechaHecho = hecho.getFechaHecho();
    this.fechaCarga = hecho.getFechaCarga();
    this.fechaModificacion = hecho.getFechaModificacion();
    this.anonimo = hecho.getAnonimo();
    this.eliminado = hecho.getEliminado();
    this.idFuente = hecho.getIdFuente();

    // Evitamos exponer todo el usuario
    this.perfil = hecho.getPerfil() != null ? hecho.getPerfil().getNombre() : null;

    // Multimedia simplificada
    this.multimedia = hecho.getMultimedia() != null
        ? hecho.getMultimedia().stream().map(MultimediaDTO::new).collect(Collectors.toList())
        : new ArrayList<>();

    // Consensos (solo nombre o tipo)
    this.consensos = hecho.getConsensos() != null
        ? hecho.getConsensos().stream().map(ConsensoDTO::new).collect(Collectors.toList())
        : new ArrayList<>();

    // Metadata (mapa plano)
    this.metadata = hecho.getMetadata() != null
        ? new LinkedHashMap<>(hecho.getMetadata())
        : new LinkedHashMap<>();
  }
}