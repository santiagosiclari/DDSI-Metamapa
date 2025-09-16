package FuenteDinamica.web;
import java.util.*;
import FuenteDinamica.DTO.HechoDTO;
import FuenteDinamica.persistencia.RepositorioFuentes;
import FuenteDinamica.business.Hechos.*;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteDinamica {
  public RepositorioFuentes repositorioFuentes;

  public ControllerFuenteDinamica(RepositorioFuentes repositorioFuentes){
    this.repositorioFuentes= repositorioFuentes;
  }

  // obtener todas las fuentes
  @GetMapping("/")
  public List<FuenteDinamica> getFuenteDeDatos(){
    return repositorioFuentes.getFuentesDinamicas();
  }

  // obtener una fuente por id
  @GetMapping("/{idFuenteDeDatos}")
  public FuenteDinamica getFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }

  // crear una fuente
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<FuenteDinamica> crearFuenteDeDatos(@RequestBody FuenteDinamica body, UriComponentsBuilder uriBuilder) {
      var creada = repositorioFuentes.agregarFuente(body);
      var loc = uriBuilder.path("/api-fuentesDeDatos/{id}").buildAndExpand(creada.getFuenteId()).toUri();
      return ResponseEntity.created(loc).body(creada);
  }

  // este me parece que no se usa, ya que el agregador se actualiza solo por ahi esta para otra cosa
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ArrayList<Hecho> getHechosFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos).getHechos();
  }

  // Cargar un hecho a una fuente
  @PostMapping (value = "/{idFuenteDeDatos}/hechos", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> cargarHecho(@PathVariable Integer idFuenteDeDatos, @Valid @RequestBody HechoDTO hechoDTO) {
    try {
      var fuente = repositorioFuentes.buscarFuente(idFuenteDeDatos);
      if (fuente == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Fuente no encontrada: " + idFuenteDeDatos));
      }

      // generar un id incremental de hecho dentro de la fuente
      int nuevoHechoId = fuente.getHechos().size() + 1;

      // construir el dominio
      Hecho hecho = hechoDTO.toDomain(idFuenteDeDatos);
      // >>> clave: setear el id del hecho <<<
      hecho.setId(nuevoHechoId);

      fuente.getHechos().add(hecho);

      // responder con un id claro que Metamapa pueda leer
      return ResponseEntity.status(HttpStatus.CREATED)
              .body(Map.of("id", nuevoHechoId));
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