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
@RequestMapping("/api-fuentesDeDatos") //Cambiar a /api-fuente
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

  // Método para convertir el JSON en un objeto Hecho y asignarle las fechas
  private Hecho crearHechoDesdeJson(String hechoJson, FuenteDinamica fuente) throws IOException {
    HechoDTO hechoDTO = new ObjectMapper().readValue(hechoJson, HechoDTO.class);
    Hecho hecho = hechoDTO.toDomain(fuente);
    hecho.setFechaCarga(LocalDateTime.now());
    hecho.setFechaModificacion(LocalDateTime.now());
    return hecho;
  }

  // Método para procesar los archivos y asociarlos al Hecho
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
    String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
    Path destino = Paths.get(baseDir).resolve(nombreArchivo);
    Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
    return nombreArchivo;
  }

  @GetMapping("/archivos/{nombreArchivo:.+}")
  public ResponseEntity<Resource> servirArchivo(@PathVariable String nombreArchivo) {
    try {
      String nombreDecodificado = URLDecoder.decode(nombreArchivo, StandardCharsets.UTF_8);
      Path ruta = Paths.get("Metamapa/M-FuenteDinamica-Service/src/main/resources/archivos")
              .resolve(nombreDecodificado)
              .normalize();
      if (!Files.exists(ruta)) {
        return ResponseEntity.notFound().build();
      }
      Resource recurso = new UrlResource(ruta.toUri());
      String tipo = Files.probeContentType(ruta);
      return ResponseEntity.ok()
              .contentType(MediaType.parseMediaType(
                      tipo != null ? tipo : "application/octet-stream"))
              .body(recurso);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().build();
    }
  }

  private TipoMultimedia deducirTipo(String contentType) {
    if (contentType.startsWith("image")) return TipoMultimedia.FOTO;
    if (contentType.startsWith("video")) return TipoMultimedia.VIDEO;
    if (contentType.startsWith("audio")) return TipoMultimedia.AUDIO;
    return null;
  }

}