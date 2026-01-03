package Estadistica.web;
import Estadistica.Service.ServiceEstadistica;
import Estadistica.business.Estadistica.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

@Controller
@Tag(name = "Servicio de Estadísticas", description = "Consultas y exportación de estadísticas")
@RequiredArgsConstructor
@RequestMapping("/")
public class ControllerEstadistica {
  private final ServiceEstadistica estadisticaService;
  private List<Map<String, String>> agregadores = new ArrayList<>();

  @PostMapping("/agregador")
  public ResponseEntity<Object> registrarAgregador(@RequestBody Map<String, Object> body) {
    try {
      Map<String, String> agregador = new HashMap<>();
      agregador.put("UrlBase", (String) body.get("UrlBase"));
      agregador.put("endpointSolicitudesEliminacion", (String) body.get("endpointSolicitudesEliminacion"));
      agregador.put("endpointHechos", (String) body.get("endpointHechos"));
      agregador.put("endpointColecciones", (String) body.get("endpointColecciones"));
      agregadores.add(agregador);
      return ResponseEntity.ok().body(agregador);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/actualizar")
  public ResponseEntity<Void> actualizarEstadisticas() {
    estadisticaService.actualizar();
    return ResponseEntity.noContent().build();
  }

  //De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados? 
  @Operation(
          summary = "Provincia con mayor cantidad de hechos reportados de una coleccion",
          description = "Devuelve el nombre de la provincia que posee la mayor cantidad de hechos registrados de la coleccion indicada."
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Provincia más reportada encontrada de una coleccion",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = String.class),
                          examples = @ExampleObject(value = "\"Incendio forestal\"")
                  )
          ),
          @ApiResponse(
                  responseCode = "204",
                  description = "No se encontraron hechos para calcular la estadística"
          )
  })
  @GetMapping("/coleccion/{uuid}/provincia-mas-reportada")
  public ResponseEntity<ProvinciaConMasHechosPorColeccion> estadisticaColeccionProvincia(@PathVariable UUID uuid) {
    ProvinciaConMasHechosPorColeccion provincia = estadisticaService.obtenerUltimaEstadisticaColeccionProvincia(uuid);
    return (provincia == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(provincia);
  }


  //¿Cuál es la categoría con mayor cantidad de hechos reportados?
  @Operation(
          summary = "Categoría con mayor cantidad de hechos reportados",
          description = "Devuelve el nombre de la categoría que posee la mayor cantidad de hechos registrados en el sistema."
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Categoría más reportada encontrada",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = String.class),
                          examples = @ExampleObject(value = "\"Incendio forestal\"")
                  )
          ),
          @ApiResponse(responseCode = "204", description = "No se encontraron hechos para calcular la estadística")
  })
  @GetMapping("/categoria")
  public ResponseEntity<CategoriaConMasHechos> obtenerCategoriaMasReportada() {
    CategoriaConMasHechos categoria = estadisticaService.obtenerUltimaEstadisticaCategoriaMasReportada();
    return (categoria == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(categoria);
  }


  // ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
  @Operation(
          summary = "En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?",
          description = "Devuelve la provincia con la mayor cantidad de hechos de la categoria indicada."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "OK"),
          @ApiResponse(responseCode = "204", description = "Sin datos con provincia disponible")
  })
  @GetMapping("/hechos/provincia-mas-reportada")
  public ResponseEntity<ProvinciaConMasHechosPorCategoria> provinciaMasReportada(@RequestParam String categoria) {
    ProvinciaConMasHechosPorCategoria provincia = estadisticaService.obtenerUltimaEstadisticaProvinciaCategoria(categoria);
    return (provincia == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(provincia);
  }

  // ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
  @Operation(
          summary = "Hora del día con más hechos para una categoría",
          description = "Devuelve la hora (0–23) en la que se registran más hechos de la categoría indicada."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "OK"),
          @ApiResponse(responseCode = "204", description = "Sin datos con hora disponible")
  })
  @GetMapping("/hechos/hora")
  public ResponseEntity<HoraConMasHechosPorCategoria> horaMasReportada(@RequestParam String categoria) {
    HoraConMasHechosPorCategoria hora = estadisticaService.obtenerUltimaEstadisticaHoraCategoria(categoria);
    return (hora == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(hora);
  }

  // ¿Cuántas solicitudes de eliminación son spam?
  @Operation(
          summary = "Cantidad de solicitudes de eliminación que son spam",
          description = "Devuelve el número total de solicitudes de eliminación marcadas como spam"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Cantidad obtenida correctamente",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
          @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
  })
  @GetMapping("/spam")
  public ResponseEntity<CantidadDeSpam> contarSolicitudesSpam() {
    CantidadDeSpam cantidad = estadisticaService.obtenerUltimaEstadisticaSpam();
    return (cantidad == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(cantidad);
  }


}