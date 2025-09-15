package Agregador.web;
import Agregador.DTO.ColeccionDTO;
import Agregador.DTO.FiltrosHechosDTO;
import Agregador.Service.ServiceColecciones;
import Agregador.business.Consenso.ModosDeNavegacion;
import Agregador.business.Hechos.Hecho;
import java.util.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-colecciones")
public class ControllerColecciones {
  private final ServiceColecciones serviceColecciones;

  public ControllerColecciones(ServiceColecciones serviceColecciones) {
    this.serviceColecciones = serviceColecciones;
  }

 /* @GetMapping("/{identificador}/hechos")
  public ResponseEntity<ArrayList<Hecho>> getHechosColeccion(
          @PathVariable("identificador") UUID identificador,
          @RequestParam(value = "modoNavegacion", required = false, defaultValue = "IRRESTRICTA") String modoNavegacion,
          @RequestParam(value = "tituloP", required = false) String tituloP,
          @RequestParam(value = "descripcionP", required = false) String descripcionP,
          @RequestParam(value = "categoriaP", required = false) String categoriaP,
          @RequestParam(value = "fecha_reporte_desdeP", required = false) String fecha_reporte_desdeP,
          @RequestParam(value = "fecha_reporte_hastaP", required = false) String fecha_reporte_hastaP,
          @RequestParam(value = "fecha_acontecimiento_desdeP", required = false) String fecha_acontecimiento_desdeP,
          @RequestParam(value = "fecha_acontecimiento_hastaP", required = false) String fecha_acontecimiento_hastaP,
          @RequestParam(value = "latitudP", required = false) String latitudP,
          @RequestParam(value = "longitudP", required = false) String longitudP,
          @RequestParam(value = "tipoMultimediaP", required = false) String tipoMultimediaP,
          @RequestParam(value = "tituloNP", required = false) String tituloNP,
          @RequestParam(value = "descripcionNP", required = false) String descripcionNP,
          @RequestParam(value = "categoriaNP", required = false) String categoriaNP,
          @RequestParam(value = "fecha_reporte_desdeNP", required = false) String fecha_reporte_desdeNP,
          @RequestParam(value = "fecha_reporte_hastaNP", required = false) String fecha_reporte_hastaNP,
          @RequestParam(value = "fecha_acontecimiento_desdeNP", required = false) String fecha_acontecimiento_desdeNP,
          @RequestParam(value = "fecha_acontecimiento_hastaNP", required = false) String fecha_acontecimiento_hastaNP,
          @RequestParam(value = "latitudNP", required = false) String latitudNP,
          @RequestParam(value = "longitudNP", required = false) String longitudNP,
          @RequestParam(value = "tipoMultimediaNP", required = false) String tipoMultimediaNP) {
    try {
      // Construir los mapas de parámetros
      Map<String, String> paramsP = new HashMap<>();
      paramsP.put("titulo", tituloP);
      paramsP.put("descripcion", descripcionP);
      paramsP.put("categoria", categoriaP);
      paramsP.put("fecha_reporte_desde", fecha_reporte_desdeP);
      paramsP.put("fecha_reporte_hasta", fecha_reporte_hastaP);
      paramsP.put("fecha_acontecimiento_desde", fecha_acontecimiento_desdeP);
      paramsP.put("fecha_acontecimiento_hasta", fecha_acontecimiento_hastaP);
      paramsP.put("latitud", latitudP);
      paramsP.put("longitud", longitudP);
      paramsP.put("tipoMultimedia", tipoMultimediaP);
      Map<String, String> paramsNP = new HashMap<>();
      paramsNP.put("titulo", tituloNP);
      paramsNP.put("descripcion", descripcionNP);
      paramsNP.put("categoria", categoriaNP);
      paramsNP.put("fecha_reporte_desde", fecha_reporte_desdeNP);
      paramsNP.put("fecha_reporte_hasta", fecha_reporte_hastaNP);
      paramsNP.put("fecha_acontecimiento_desde", fecha_acontecimiento_desdeNP);
      paramsNP.put("fecha_acontecimiento_hasta", fecha_acontecimiento_hastaNP);
      paramsNP.put("latitud", latitudNP);
      paramsNP.put("longitud", longitudNP);
      paramsNP.put("tipoMultimedia", tipoMultimediaNP);
      ArrayList<Hecho> hechos = this.serviceColecciones.getHechosColeccion(identificador, modoNavegacion, paramsP, paramsNP);
      return ResponseEntity.ok(hechos);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
    } catch (Exception e) {
      System.err.println("Error al obtener hechos de colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
    }
  }*/

  @GetMapping("/{identificador}/hechos")
  public ResponseEntity<List<Hecho>> getHechosColeccion(@PathVariable UUID identificador,
                                                        @RequestParam(defaultValue = "IRRESTRICTA") ModosDeNavegacion modoNavegacion,
                                                        @Valid FiltrosHechosDTO filtros
  ) {
    List<Hecho> hechos = serviceColecciones.getHechosFiltrados(identificador, modoNavegacion, filtros);
    return ResponseEntity.ok(hechos);
  }

  // Obtener todas las colecciones (get /colecciones)
  @GetMapping({"", "/"})
  public ResponseEntity<List<ColeccionDTO>> obtenerTodasLasColecciones() {
    try {
      return ResponseEntity.ok(serviceColecciones.obtenerTodasLasColecciones());
    } catch (Exception e) {
      System.err.println("Error al obtener colecciones: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Obtener una colección por ID (get /colecciones/{id})
  @GetMapping("/{id}")
  public ResponseEntity<ColeccionDTO> obtenerColeccionPorId(@PathVariable("id") UUID id) {
    try {
      return ResponseEntity.ok(serviceColecciones.obtenerColeccionPorId(id));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      System.err.println("Error al obtener colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Crear una coleccion (post /colecciones)
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<ColeccionDTO> crearColeccion(@Valid @RequestBody ColeccionDTO dto) {
    ColeccionDTO creada = serviceColecciones.crearColeccion(dto);
    System.out.println("9004 → crearColeccion.consenso = " + dto.getConsenso());
    return ResponseEntity.status(HttpStatus.CREATED).body(creada);
  }

  @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> actualizarColeccion(@PathVariable("id") UUID id, @Valid @RequestBody ColeccionDTO requestBody) {
    try {
      return ResponseEntity.ok(this.serviceColecciones.actualizarColeccion(id, requestBody));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      System.err.println("Error al actualizar colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Modificar algoritmo de consenso (patch /colecciones/{id})
  @PatchMapping(value = "/{id}", consumes = "application/json")
  public ResponseEntity<?> modificarAlgoritmo(@PathVariable UUID id,
                                              @RequestBody Map<String, Object> requestBody) {
    try {
      serviceColecciones.modificarAlgoritmo(id, requestBody);
      return ResponseEntity.noContent().build(); // 204
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(Map.of("error", e.getMessage())); // 404
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
              .body(Map.of("error", e.getMessage())); // 400
    } catch (Exception e) {
      System.err.println("Error al modificar algoritmo: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("error", "Error interno")); // 500
    }
  }

  // Eliminar una colección (delete /colecciones/{id})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarColeccion(@PathVariable UUID id) {
    boolean ok = serviceColecciones.eliminarColeccion(id);
    return ok ? ResponseEntity.noContent().build() // 204
            : ResponseEntity.notFound().build(); // 404 si no existía
  }

  @PostMapping("/fuentesDeDatos/{idColeccion}/{idFuente}")
  public ResponseEntity<?> agregarFuente(@PathVariable UUID idColeccion, @PathVariable Integer idFuente) {
    try {
      serviceColecciones.agregarFuenteDeDatos(idColeccion, idFuente);
      return ResponseEntity.ok("fuente agregada a la coleccion");
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(404).body("Colección no encontrada: " + idColeccion);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error interno");
    }
  }

  @PostMapping("/fuentesDeDatos/{idColeccion}/remover/{idFuente}")
  public ResponseEntity<?> eliminarFuente(@PathVariable Integer idFuente, @PathVariable String idColeccion) {
    try {
      serviceColecciones.eliminarFuenteDeDatos(UUID.fromString(idColeccion), idFuente);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }
}