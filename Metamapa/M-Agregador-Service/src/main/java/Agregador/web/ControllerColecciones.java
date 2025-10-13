package Agregador.web;
import Agregador.DTO.*;
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
  public ResponseEntity<?> modificarAlgoritmo(@PathVariable UUID id, @RequestBody Map<String, Object> requestBody) {
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
  public ResponseEntity<?> eliminarColeccion(@PathVariable UUID id) {
   try {
     serviceColecciones.eliminarColeccion(id);
     return ResponseEntity.noContent().build(); // 204
   } catch (NoSuchElementException e) {
     return ResponseEntity.status(404).body("Colección no encontrada: " + id);
   } catch (Exception e) {
     return ResponseEntity.status(500).body("Error interno");
   }
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