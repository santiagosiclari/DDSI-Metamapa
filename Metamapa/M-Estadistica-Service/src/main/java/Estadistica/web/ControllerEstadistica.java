package Estadistica.web;

import Estadistica.Service.ServiceEstadistica;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.nio.charset.StandardCharsets;

@Controller
@Tag(name = "Servicio de Estadísticas", description = "Consultas y exportación de estadísticas")
@RequestMapping("/estadistica")
public class ControllerEstadistica {
  private final ServiceEstadistica estadisticaService;
  //TODO: tendra repositorio? no dice nada la consigna

  public ControllerEstadistica(ServiceEstadistica estadisticaService) {
    this.estadisticaService = estadisticaService;
  }

  @PostMapping("/actualizar")
  public ResponseEntity<Void> actualizarEstadisticas() {
    estadisticaService.actualizar();
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/actualizar-ui")
  public ResponseEntity<Void> actualizarEstadisticaUI() {
    estadisticaService.actualizarDashboards();
    return ResponseEntity.accepted().build();
  }
  // Se elimina creo
  public void generarEstadisticas() {}

  //TODO: De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados? 
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
  public ResponseEntity<String> provinciaMasReportada(@PathVariable UUID uuid) {
    String provinica = estadisticaService.provinciaMasReportadaDeColeccion(uuid);
    return (provinica == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(provinica);
  }

  //TODO revisar:¿Cuál es la categoría con mayor cantidad de hechos reportados?
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
          @ApiResponse(
                  responseCode = "204",
                  description = "No se encontraron hechos para calcular la estadística"
          )
  })
  @GetMapping("/categoria")
  public ResponseEntity<String> obtenerCategoriaMasReportada() {
    String categoria = estadisticaService.obtenerCategoriaMasReportada();
    return (categoria == null || categoria.isBlank())
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(categoria);
  }


  //TODO: ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
  @Operation(
      summary = "En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?",
      description = "Devuelve la provincia con la mayor cantidad de hechos de la categoria indicada."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "204", description = "Sin datos con provincia disponible")
  })
  @GetMapping("/hechos/provincia-mas-reportada")
  public ResponseEntity<String> provinciaMasReportada(@RequestParam String categoria) {
    String provinica = estadisticaService.provinciaMasReportada(categoria);
    return (provinica == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(provinica);
  }

  //TODO: ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
  @Operation(
          summary = "Hora del día con más hechos para una categoría",
          description = "Devuelve la hora (0–23) en la que se registran más hechos de la categoría indicada."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "OK"),
          @ApiResponse(responseCode = "204", description = "Sin datos con hora disponible")
  })
  @GetMapping("/hechos/hora")
  public ResponseEntity<Integer> horaMasReportada(@RequestParam String categoria) {
    Integer hora = estadisticaService.horaMasReportada(categoria);
    return (hora == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(hora);
  }

  //TODO revisar: ¿Cuántas solicitudes de eliminación son spam?
  @Operation(
          summary = "Cantidad de solicitudes de eliminación que son spam",
          description = "Devuelve el número total de solicitudes de eliminación marcadas como spam"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Cantidad obtenida correctamente",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = Long.class))),
          @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                  content = @Content)
  })
  @GetMapping("/spam")
  public ResponseEntity<Long> contarSolicitudesSpam() {
    try {
      long cantidad = estadisticaService.getSolicitudesSpam();
      return ResponseEntity.ok(cantidad);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
    }
  }

  //TODO: Se deberá implementar la exportación de las estadísticas en formato CSV.
  @Operation(summary = "Exportación CSV genérica")
  @ApiResponse(responseCode = "200", description = "CSV",
          content = @Content(mediaType = "text/csv"))
  @GetMapping(value = "/export", produces = "text/csv")
  public ResponseEntity<byte[]> exportarDatos(){
    String csv = estadisticaService.exportarCsv();
    byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ".csv\"")
        .contentType(MediaType.valueOf("text/csv"))
        .contentLength(bytes.length)
        .body(bytes);
  }

}
