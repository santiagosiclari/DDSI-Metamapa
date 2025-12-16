package Agregador.web;
import Agregador.Service.ServiceAgregador;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-hechos")
public class ControllerHechos {
  private final ServiceAgregador service;

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