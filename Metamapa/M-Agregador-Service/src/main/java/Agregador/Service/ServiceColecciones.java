package Agregador.Service;
import Agregador.DTO.*;
import Agregador.business.Colecciones.*;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.*;
import Agregador.persistencia.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceColecciones {
  public final RepositorioColecciones repositorioColecciones;
  public final RepositorioHechos repositorioHechos;

  public ServiceColecciones(RepositorioColecciones repositorioColecciones, RepositorioHechos repositorioHechos) {
    this.repositorioHechos = repositorioHechos;
    this.repositorioColecciones = repositorioColecciones;
  }

  public ArrayList<Hecho> getHechosColeccion(UUID id, String modoNavegacion, Map<String, String> paramsP, Map<String, String> paramsNP) {
    Coleccion coleccion = repositorioColecciones.buscarXUUID(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    ModosDeNavegacion modo = ModosDeNavegacion.valueOf(modoNavegacion.toUpperCase());
    ArrayList<Criterio> criteriosP = (ArrayList<Criterio>) procesarCriterios(
            construirCriteriosJson(
                    paramsP.get("titulo"),
                    paramsP.get("descripcion"),
                    paramsP.get("categoria"),
                    paramsP.get("fecha_reporte_desde"),
                    paramsP.get("fecha_reporte_hasta"),
                    paramsP.get("fecha_acontecimiento_desde"),
                    paramsP.get("fecha_acontecimiento_hasta"),
                    paramsP.get("latitud"),
                    paramsP.get("longitud"),
                    paramsP.get("tipoMultimedia")
            )
    );
    ArrayList<Criterio> criteriosNP = (ArrayList<Criterio>) procesarCriterios(
            construirCriteriosJson(
                    paramsNP.get("titulo"),
                    paramsNP.get("descripcion"),
                    paramsNP.get("categoria"),
                    paramsNP.get("fecha_reporte_desde"),
                    paramsNP.get("fecha_reporte_hasta"),
                    paramsNP.get("fecha_acontecimiento_desde"),
                    paramsNP.get("fecha_acontecimiento_hasta"),
                    paramsNP.get("latitud"),
                    paramsNP.get("longitud"),
                    paramsNP.get("tipoMultimedia")
            )
    );
     return coleccion.filtrarPorCriterios((ArrayList<Hecho>) repositorioHechos.getHechos(), criteriosP, criteriosNP, modo);
  }

  public List<ColeccionDTO> obtenerTodasLasColecciones() {
    return repositorioColecciones.getColecciones().stream()
            .map(ColeccionDTO::new) // convierte cada Coleccion a DTO
            .collect(Collectors.toList());
  }

  public ColeccionDTO obtenerColeccionPorId(UUID id) {
    Coleccion coleccion = repositorioColecciones.buscarXUUID(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    return new ColeccionDTO(coleccion);
  }

  public ColeccionDTO crearColeccion(ColeccionDTO coleccionDTO) {
    ArrayList<Criterio> pertenencia = coleccionDTO.getCriteriosPertenencia().stream()
            .map(CriterioDTO::toDomain)
            .collect(Collectors.toCollection(ArrayList::new));
    ArrayList<Criterio> noPertenencia = coleccionDTO.getCriteriosNoPertenencia().stream()
            .map(CriterioDTO::toDomain)
            .collect(Collectors.toCollection(ArrayList::new));
    Coleccion coleccion = new Coleccion(coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), pertenencia, noPertenencia);
    if (coleccionDTO.getConsenso() != null) {
      coleccion.setConsenso(Consenso.stringToConsenso(coleccionDTO.getConsenso()));
    }
    repositorioColecciones.getColecciones().add(coleccion);
    return new ColeccionDTO(coleccion);
  }

  public ColeccionDTO actualizarColeccion(UUID id, ColeccionDTO coleccionDTO) {
    Coleccion coleccion = repositorioColecciones.buscarXUUID(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    if (coleccionDTO.getTitulo() != null) coleccion.setTitulo(coleccionDTO.getTitulo());
    if (coleccionDTO.getDescripcion() != null) coleccion.setDescripcion(coleccionDTO.getDescripcion());
    if (coleccionDTO.getConsenso() != null) coleccion.setConsenso(Consenso.stringToConsenso(coleccionDTO.getConsenso()));
    if (coleccionDTO.getCriteriosPertenencia() != null) {
      List<Criterio> nuevosCriterios = coleccionDTO.getCriteriosPertenencia().stream()
              .map(CriterioDTO::toDomain)
              .toList();
      coleccion.setCriterioPertenencia(new ArrayList<>(nuevosCriterios));
    }
    if (coleccionDTO.getCriteriosNoPertenencia() != null) {
      List<Criterio> nuevosCriterios = coleccionDTO.getCriteriosNoPertenencia().stream()
              .map(CriterioDTO::toDomain)
              .toList();
      coleccion.setCriterioNoPertenencia(new ArrayList<>(nuevosCriterios));
    }
    return new ColeccionDTO(coleccion);
  }

  public String modificarAlgoritmo(UUID id, Map<String, Object> requestBody) {
    Coleccion coleccion = repositorioColecciones.buscarXUUID(id)
            .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
    String consensoStr = (String) requestBody.get("consenso");
    //if (consensoStr == null)
    //  return ResponseEntity.badRequest().body(Map.of("error", "El campo 'consenso' es obligatorio"));
    coleccion.setConsenso(Consenso.stringToConsenso(consensoStr));
    return "Algoritmo de consenso actualizado a " + consensoStr;
  }

  public boolean eliminarColeccion(UUID id) {
    return repositorioColecciones.eliminar(id);
  }

  // Metodo auxiliar para construir criterios JSON desde parámetros
  private List<Map<String, Object>> construirCriteriosJson(
          String titulo, String descripcion, String categoria,
          String fechaReporteDesde, String fechaReporteHasta,
          String fechaAcontecimientoDesde, String fechaAcontecimientoHasta,
          String latitud, String longitud, String tipoMultimedia) {
    List<Map<String, Object>> criterios = new ArrayList<>();
    agregarCriterio(criterios, "titulo", "valor", titulo);
    agregarCriterio(criterios, "descripcion", "valor", descripcion);
    agregarCriterio(criterios, "categoria", "valor", categoria);
    if (fechaReporteDesde != null || fechaReporteHasta != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "fechareportaje");
      if (fechaReporteDesde != null) criterio.put("fechaDesde", fechaReporteDesde);
      if (fechaReporteHasta != null) criterio.put("fechaHasta", fechaReporteHasta);
      criterios.add(criterio);
    }
    if (fechaAcontecimientoDesde != null || fechaAcontecimientoHasta != null) {
      Map<String, Object> criterio = new HashMap<>();
      criterio.put("tipo", "fecha");
      if (fechaAcontecimientoDesde != null) criterio.put("fechaDesde", fechaAcontecimientoDesde);
      if (fechaAcontecimientoHasta != null) criterio.put("fechaHasta", fechaAcontecimientoHasta);
      criterios.add(criterio);
    }
    if (latitud != null && longitud != null) {
      criterios.add(Map.of(
              "tipo", "ubicacion",
              "latitud", latitud,
              "longitud", longitud
      ));
    }
    if (tipoMultimedia != null) {
      criterios.add(Map.of(
              "tipo", "multimedia",
              "tipoMultimedia", tipoMultimedia
      ));
    }
    return criterios;
  }

  private void agregarCriterio(List<Map<String, Object>> criterios, String tipo, String claveValor, String valor) {
    if (valor != null) {
      criterios.add(Map.of(
              "tipo", tipo,
              claveValor, valor
      ));
    }
  }

  private List<Criterio> procesarCriterios(List<Map<String, Object>> criteriosJson) {
    return criteriosJson.stream()
            .map(this::crearCriterioDesdeJson)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  private Criterio crearCriterioDesdeJson(Map<String, Object> criterioJson) {
    String tipo = ((String) criterioJson.get("tipo")).toLowerCase();
    try {
      switch (tipo) {
        case "titulo":
          return new CriterioTitulo((String) criterioJson.get("valor"));
        case "descripcion":
          return new CriterioDescripcion((String) criterioJson.get("valor"));
        case "categoria":
          return new CriterioCategoria((String) criterioJson.get("valor"));
        case "fecha":
          LocalDate desde = parseFecha((String) criterioJson.get("fechaDesde"));
          LocalDate hasta = parseFecha((String) criterioJson.get("fechaHasta"));
          return new CriterioFecha(desde, hasta);
        case "fechareportaje":
          LocalDate desdeRpt = parseFecha((String) criterioJson.get("fechaDesde"));
          LocalDate hastaRpt = parseFecha((String) criterioJson.get("fechaHasta"));
          return new CriterioFechaReportaje(desdeRpt, hastaRpt);
        case "ubicacion":
          Float lat = parseFloat(criterioJson.get("latitud"));
          Float lon = parseFloat(criterioJson.get("longitud"));
          if (lat != null && lon != null) {
            return new CriterioUbicacion(lat, lon);
          } else {
            return null;
          }
        case "multimedia":
          String tipoMultimediaStr = (String) criterioJson.get("tipoMultimedia");
          if (tipoMultimediaStr != null) {
            TipoMultimedia tipoMultimedia = TipoMultimedia.valueOf(tipoMultimediaStr.toUpperCase());
            return new CriterioMultimedia(tipoMultimedia);
          } else {
            return null;
          }
        default:
          System.err.println("Tipo de criterio no reconocido: " + tipo);
          return null;
      }
    } catch (Exception e) {
      System.err.println("Error procesando criterio: " + e.getMessage());
      return null;
    }
  }

  private LocalDate parseFecha(String str) {
    return str != null ? LocalDate.parse(str) : null;
  }

  private Float parseFloat(Object obj) {
    return obj != null ? Float.parseFloat(obj.toString()) : null;
  }
}