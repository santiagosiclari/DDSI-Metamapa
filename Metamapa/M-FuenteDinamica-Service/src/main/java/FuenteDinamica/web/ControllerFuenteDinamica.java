package FuenteDinamica.web;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import FuenteDinamica.DTO.*;
import FuenteDinamica.persistencia.*;
import FuenteDinamica.business.Hechos.*;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteDinamica {
  public RepositorioFuentes repositorioFuentes;
  public RepositorioHechos repositorioHechos;
  private final String baseDir = "/app/multimedia/";

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

  // Cargar un hecho a una fuente
  @PostMapping(value = "/{idFuenteDeDatos}/hechos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> cargarHecho(@PathVariable Integer idFuenteDeDatos, @RequestPart("hecho") String hechoJson,
                                       @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
    try {
      FuenteDinamica fuente = repositorioFuentes.findById(idFuenteDeDatos)
              .orElseThrow(() -> new IllegalArgumentException("Fuente no encontrada: " + idFuenteDeDatos));
      Hecho hecho = crearHechoDesdeJson(hechoJson, fuente);
      if (archivos != null && !archivos.isEmpty())
        procesarArchivos(archivos, hecho);
      repositorioHechos.save(hecho);
      return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", hecho.getId()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("error", "Error interno: " + e.getMessage()));
    }
  }

  private Hecho crearHechoDesdeJson(String hechoJson, FuenteDinamica fuente) throws IOException {
    HechoDTO hechoDTO = new ObjectMapper().readValue(hechoJson, HechoDTO.class);
    Hecho hecho = hechoDTO.toDomain(fuente);
    hecho.setFechaCarga(LocalDateTime.now());
    hecho.setFechaModificacion(LocalDateTime.now());
    return hecho;
  }

  private void procesarArchivos(List<MultipartFile> archivos, Hecho hecho) throws IOException {
    for (MultipartFile archivo : archivos) {
      if (!archivo.isEmpty()) {
        String nombreArchivo = guardarArchivoEnDisco(archivo);
        Multimedia mm = new Multimedia();
        mm.setPath(nombreArchivo);
        mm.setTipoMultimedia(deducirTipo(Objects.requireNonNull(archivo.getContentType())));
        mm.setHecho(hecho);
        hecho.agregarMultimedia(mm);
      }
    }
  }

  private String guardarArchivoEnDisco(MultipartFile archivo) throws IOException {
    File directorio = new File(baseDir);
    if (!directorio.exists()) {
      directorio.mkdirs();
    }

    String originalName = Objects.requireNonNull(archivo.getOriginalFilename());

    String extension = "";
    String nombreSinExtension = originalName;
    int i = originalName.lastIndexOf('.');
    if (i > 0) {
      extension = originalName.substring(i).toLowerCase(); // .jpg
      nombreSinExtension = originalName.substring(0, i);
    }

    String nombreLimpio = nombreSinExtension.toLowerCase()
            .replaceAll("[^a-z0-9]", "_") // Reemplaza todo lo raro por _
            .replaceAll("_+", "_");      // Evita múltiples guiones bajos seguidos

    String nombreFinal = System.currentTimeMillis() + "_" + nombreLimpio + extension;

    Path destino = Paths.get(baseDir).resolve(nombreFinal);
    Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

    return nombreFinal;
  }

  private TipoMultimedia deducirTipo(String contentType) {
    if (contentType.startsWith("image")) return TipoMultimedia.FOTO;
    if (contentType.startsWith("video")) return TipoMultimedia.VIDEO;
    if (contentType.startsWith("audio")) return TipoMultimedia.AUDIO;
    return null;
  }

}