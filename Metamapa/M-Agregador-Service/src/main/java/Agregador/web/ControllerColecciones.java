package Agregador.web;
import Agregador.DTO.*;
import Agregador.Service.ServiceColecciones;
import Agregador.business.Colecciones.Coleccion;
import Agregador.business.Consenso.ModosDeNavegacion;
import Agregador.business.Hechos.Hecho;
import java.util.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ControllerColecciones {
  private final ServiceColecciones serviceColecciones;

  @GetMapping("/{id}/hechos")
  public ResponseEntity<?> getHechosColeccion(
          @PathVariable UUID id,
          @RequestParam(defaultValue = "IRRESTRICTA") ModosDeNavegacion modoNavegacion,
          @Valid FiltrosHechosDTO filtros) {
    List<Hecho> hechos = serviceColecciones.getHechosFiltrados(id, modoNavegacion, filtros);
    return ResponseEntity.ok(hechos);
  }

  // Obtener todas las colecciones (get /colecciones)
  @GetMapping({"", "/"})
  public ResponseEntity<List<Coleccion>> getColecciones(
          @RequestParam(value = "query", required = false) String query
  ) {
    return ResponseEntity.ok(serviceColecciones.getColecciones(query));
  }

  // Obtener una colección por ID (get /colecciones/{id})
  @GetMapping("/{id}")
  public ResponseEntity<Optional<Coleccion>> getColeccion(@PathVariable UUID id) {
    Optional<Coleccion> coleccion = serviceColecciones.getColeccion(id);
    return ResponseEntity.ok(coleccion);
  }

  // Crear una coleccion (post /colecciones)
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<ColeccionDTO> crearColeccion(@Valid @RequestBody ColeccionDTO dto) {
    ColeccionDTO creada = serviceColecciones.crearColeccion(dto);
    System.out.println("9004 → crearColeccion.consenso = " + dto.getConsenso());
    return ResponseEntity.status(HttpStatus.CREATED).body(creada);
  }

  @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<ColeccionDTO> actualizarColeccion(@PathVariable UUID id, @Valid @RequestBody ColeccionDTO dto) {
    ColeccionDTO actualizada = serviceColecciones.actualizarColeccion(id, dto);
    return ResponseEntity.ok(actualizada);
  }

  // Modificar algoritmo de consenso (patch /colecciones/{id})
  @PatchMapping(value = "/{id}", consumes = "application/json")
  public ResponseEntity<?> modificarAlgoritmo(@PathVariable UUID id, @RequestBody Map<String, Object> requestBody) {
    serviceColecciones.modificarAlgoritmo(id, requestBody);
    return ResponseEntity.noContent().build();
  }

  // Eliminar una colección (delete /colecciones/{id})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarColeccion(@PathVariable UUID id) {
    serviceColecciones.eliminarColeccion(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/colecciones/{idColeccion}/fuentes/{idFuente}")
  public ResponseEntity<String> agregarFuente(@PathVariable UUID idColeccion, @PathVariable Integer idFuente) {
    serviceColecciones.agregarFuenteDeDatos(idColeccion, idFuente);
    return ResponseEntity.ok("Fuente agregada a la colección");
  }

  @DeleteMapping("/colecciones/{idColeccion}/fuentes/{idFuente}")
  public ResponseEntity<Void> eliminarFuente(@PathVariable UUID idColeccion, @PathVariable Integer idFuente) {
    serviceColecciones.eliminarFuenteDeDatos(idColeccion, idFuente);
    return ResponseEntity.noContent().build();
  }
}