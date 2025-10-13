package Agregador.web;
import Agregador.Service.ServiceAgregador;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-hechos")
public class ControllerHechos {
  private final ServiceAgregador service;

  public ControllerHechos(ServiceAgregador service) { this.service = service; }

  // Endpoint específico para la estadística solicitada
  @GetMapping("/categoria-mas-reportada")
  public ResponseEntity<String> categoriaMasReportada(){
    var categoria = service.categoriaMasReportada();
    return (categoria == null || categoria.isBlank())
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(categoria);
  }

  @GetMapping("/hora")
  public ResponseEntity<Integer> horaMasReportada(@RequestParam String categoria) {
    Integer hora = service.horaMasReportada(categoria);
    return (hora == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(hora);
  }
}