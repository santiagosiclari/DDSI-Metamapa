package FuenteDinamica.web;
import java.time.LocalDate;
import java.util.*;
import FuenteDinamica.DTO.*;
import FuenteDinamica.persistencia.*;
import FuenteDinamica.business.Hechos.*;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteDinamica {
  public RepositorioFuentes repositorioFuentes;
  public RepositorioHechos repositorioHechos;

  public ControllerFuenteDinamica(RepositorioFuentes repositorioFuentes, RepositorioHechos repositorioHechos) {
    this.repositorioHechos = repositorioHechos;
    this.repositorioFuentes = repositorioFuentes;
  }

  // obtener todas las fuentes
  @GetMapping("/")
  public List<FuenteDTO> getFuenteDeDatos() {
    return repositorioFuentes.findAll().stream()
            .map(FuenteDTO::new)
            .toList();
  }

  // obtener una fuente por id
  @GetMapping("/{idFuenteDeDatos}")
  public ResponseEntity<?> getFuente(@PathVariable Integer idFuenteDeDatos) {
    FuenteDinamica fuente = repositorioFuentes.findById(idFuenteDeDatos).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Fuente no encontrada")
    );
    return ResponseEntity.ok(new FuenteDTO(fuente));
  }

  // crear una fuente
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<FuenteDinamica> crearFuenteDeDatos(@RequestBody FuenteDinamica body, UriComponentsBuilder uriBuilder) {
      var creada = repositorioFuentes.save(body);
      var loc = uriBuilder.path("/api-fuentesDeDatos/{id}").buildAndExpand(creada.getFuenteId()).toUri();
      return ResponseEntity.created(loc).body(creada);
  }

  // obtener todos los hechos
  @GetMapping("/hechos")
  public List<HechoDTOResponse> getHechos() {
    return repositorioHechos.findAll().stream()
            .map(HechoDTOResponse::new)
            .toList();
  }

  // este me parece que no se usa, ya que el agregador se actualiza solo por ahi esta para otra cosa
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ResponseEntity<List<HechoDTO>> getHechosFuenteDeDatos(@PathVariable Integer idFuenteDeDatos) {
    return repositorioFuentes.findById(idFuenteDeDatos)
            .map(fuente -> ResponseEntity.ok(
                    fuente.getHechos().stream()
                            .map(HechoDTO::fromDomain)
                            .toList()
            ))
            .orElse(ResponseEntity.notFound().build());
  }

  // Cargar un hecho a una fuente
  @PostMapping (value = "/{idFuenteDeDatos}/hechos", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> cargarHecho(@PathVariable Integer idFuenteDeDatos, @Valid @RequestBody HechoDTO hechoDTO) {
    try {
      FuenteDinamica fuente = repositorioFuentes.findById(idFuenteDeDatos).orElse(null);
      if (fuente == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Fuente no encontrada: " + idFuenteDeDatos));
      }
      // generar un id incremental de hecho dentro de la fuente
      //int nuevoHechoId = fuente.getHechos().size() + 1;
      // Convertir DTO a dominio
      Hecho hecho = hechoDTO.toDomain(fuente);
      hecho.setFechaCarga(LocalDate.now());
      hecho.setFechaModificacion(LocalDate.now());
      /*hecho.setId(nuevoHechoId);
      fuente.getHechos().add(hecho);*/
      // Persistir directamente el hecho
      repositorioHechos.save(hecho);
      // Devolver el ID generado
      return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", hecho.getId()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("error", "Error interno: " + e.getMessage()));
    }
  }

  public void publicarmeAAgregador() {
    String url = String.format("%s/fuenteDeDatos", "${M.Agregador.Service.url}");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String body = """
        {
            "URLBase": """ + "${M.FuenteEstatica.Service.url}" + """
        }
    """;

    HttpEntity<String> request = new HttpEntity<>(body, headers);
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.postForObject(url, request, String.class);
  }
}