package Metamapa.web;

import Metamapa.service.ServiceColecciones;
import Metamapa.service.ServiceFuenteDeDatos;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/metamapa")
public class ControllerMetamapaUI {
  private final ServiceFuenteDeDatos serviceFuenteDeDatos;
  private final ServiceColecciones serviceColecciones;

  public ControllerMetamapaUI(ServiceFuenteDeDatos serviceFuenteDeDatos, ServiceColecciones serviceColecciones) {
    this.serviceFuenteDeDatos = serviceFuenteDeDatos;
    this.serviceColecciones = serviceColecciones;
  }

  @GetMapping("/fuentesDeDatos/")
  public String listar(Model model, @ModelAttribute("success") String success) {
    // ⚠️ usamos la lista plana para la UI
    var fuentesUI = Optional.ofNullable(serviceFuenteDeDatos.getFuentesTabla()).orElseGet(List::of);

    model.addAttribute("fuentesDeDatos", fuentesUI); // <- ahora es List<Map<String,Object>>
    model.addAttribute("fuenteForm", new FuenteForm());
    return "fuentesDeDatos";
  }


  @PostMapping(value = "/fuentesDeDatos/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String crear(@Valid @ModelAttribute("fuenteForm") FuenteForm form,
                      BindingResult br,
                      RedirectAttributes ra,
                      Model model) {
    if (br.hasErrors()) {
      var fuentes = serviceFuenteDeDatos.getFuentesDeDatos();
      model.addAttribute("fuentesDeDatos", fuentes);
      return "fuentesDeDatos";
    }
    Integer id = serviceFuenteDeDatos.crearFuenteYRetornarId(form.getTipo(), form.getNombre(), form.getUrl());
    ra.addFlashAttribute("success", "Fuente creada con id: " + id);
    return "redirect:/metamapa/fuentesDeDatos/";
  }

  @GetMapping(value = "/fuentesDeDatos/{id}", produces = MediaType.TEXT_HTML_VALUE)
  public String detalle(@PathVariable Integer id, Model model, RedirectAttributes ra) {
    var fuente = serviceFuenteDeDatos.getFuenteDeDatos(id);
    if (fuente == null) {
      ra.addFlashAttribute("success", "La fuente no existe.");
      return "redirect:/metamapa/fuentesDeDatos/";
    }
    model.addAttribute("fuente", fuente);
    return "fuenteDeDatos";
  }

  //VISTA
  @GetMapping(value = {"/colecciones", "/colecciones/"}, produces = MediaType.TEXT_HTML_VALUE)
  public String listarHtml(Model model,
                           @ModelAttribute("success") String ok,
                           @ModelAttribute("error") String err) {
    model.addAttribute("colecciones", serviceColecciones.getColecciones());
    model.addAttribute("algoritmosConsenso", List.of("Absoluto", "MayoriaSimple", "MultiplesMenciones"));
    return "colecciones"; // templates/colecciones.html
  }

  @GetMapping(value = "/colecciones/{uuid}", produces = MediaType.TEXT_HTML_VALUE)
  public String verHtml(@PathVariable UUID uuid, Model model, RedirectAttributes ra) {
    var c = serviceColecciones.getColeccion(uuid);
    if (c == null) {
      ra.addFlashAttribute("error", "Colección no encontrada");
      return "redirect:/metamapa/colecciones/";
    }
    model.addAttribute("coleccion", c);
    model.addAttribute("algoritmosConsenso", List.of("Absoluto", "MayoriaSimple", "MultiplesMenciones"));
    return "detalle"; // templates/detalle.html
  }

  @PostMapping(value = "/colecciones", produces = MediaType.TEXT_HTML_VALUE,
          consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String crearColeccionHtml(@RequestParam String titulo,
                                   @RequestParam String descripcion,
                                   @RequestParam(required = false) String consenso,
                                   @RequestParam(required = false) List<String> pertenenciaTitulos,
                                   @RequestParam(required = false) List<String> noPertenenciaTitulos,
                                   RedirectAttributes ra) {
    String c = (consenso == null || consenso.isBlank()) ? "MayoriaSimple" : consenso;
    var pert = new ArrayList<Map<String, Object>>();
    if (pertenenciaTitulos != null)
      for (var t : pertenenciaTitulos) if (!t.isBlank()) pert.add(Map.of("tipo", "titulo", "valor", t.trim()));
    var noPert = new ArrayList<Map<String, Object>>();
    if (noPertenenciaTitulos != null)
      for (var t : noPertenenciaTitulos) if (!t.isBlank()) noPert.add(Map.of("tipo", "titulo", "valor", t.trim()));

    var id = serviceColecciones.crearColeccion(titulo.trim(), descripcion.trim(), c, pert, noPert);
    ra.addFlashAttribute("success", "Colección creada (ID: " + id + ")");
    return "redirect:/metamapa/colecciones/" + id;
  }

  @DeleteMapping(value = "/colecciones/{uuid}", produces = MediaType.TEXT_HTML_VALUE)
  public String eliminarHtml(@PathVariable UUID uuid, RedirectAttributes ra) {
    var status = serviceColecciones.deleteColeccion(uuid);
    if (status == HttpStatus.NO_CONTENT) ra.addFlashAttribute("success", "Colección eliminada");
    else if (status == HttpStatus.NOT_FOUND) ra.addFlashAttribute("error", "Colección no encontrada");
    else ra.addFlashAttribute("error", "No se pudo eliminar (status: " + status + ")");
    return "redirect:/metamapa/colecciones/";
  }

  // PATCH (HTML): cambia consenso leyendo @RequestParam algoritmo y redirige
  @PatchMapping(value = "/colecciones/{uuid}/consenso",
          consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
          produces = MediaType.TEXT_HTML_VALUE)
  public String cambiarConsensoHtml(@PathVariable UUID uuid,
                                    @RequestParam String algoritmo,
                                    RedirectAttributes ra) {
    var status = serviceColecciones.actualizarAlgoritmoConsenso(uuid, algoritmo);
    if (status.is2xxSuccessful()) {
      ra.addFlashAttribute("success", "Consenso actualizado a " + algoritmo);
    } else if (status == HttpStatus.NOT_FOUND) {
      ra.addFlashAttribute("error", "Colección no encontrada");
    } else {
      ra.addFlashAttribute("error", "No se pudo actualizar el consenso (status: " + status.value() + ")");
    }
    return "redirect:/metamapa/colecciones";
  }

  // Dashboard con 3 pestañas: crear eliminación, crear edición, consultar/resolver
  @GetMapping("/solicitudes")
  public String dashboard() {
    return "solicitudes"; // resources/templates/solicitudes.html
  }

  @Getter
  @Setter
  public static class FuenteForm {
    @NotBlank
    private String tipo;
    @NotBlank
    private String nombre;
    private String url;
  }
}