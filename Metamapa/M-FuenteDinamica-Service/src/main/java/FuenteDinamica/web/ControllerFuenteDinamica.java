package FuenteDinamica.web;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import FuenteDinamica.DTO.*;
import FuenteDinamica.persistencia.*;
import FuenteDinamica.business.Hechos.*;
import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api-fuentesDeDatos") //Cambuiar a /api-fuente
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
  @PostMapping(value = "/{idFuenteDeDatos}/hechos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> cargarHecho(@PathVariable Integer idFuenteDeDatos, @RequestPart("hecho") String hechoJson,
                                       @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
    try {
      // 1️⃣ Buscar fuente
      FuenteDinamica fuente = repositorioFuentes.findById(idFuenteDeDatos).orElse(null);
      if (fuente == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Fuente no encontrada: " + idFuenteDeDatos));
      }
      // 2️⃣ Parsear JSON recibido
      ObjectMapper mapper = new ObjectMapper();
      HechoDTO hechoDTO = mapper.readValue(hechoJson, HechoDTO.class);
      // 3️⃣ Convertir a entidad y completar fechas
      Hecho hecho = hechoDTO.toDomain(fuente);
      hecho.setFechaCarga(LocalDateTime.now());
      hecho.setFechaModificacion(LocalDateTime.now());
      // 2️⃣ Guardar archivos en resources/archivos
      String uploadDir = "Metamapa/M-FuenteDinamica-Service/src/main/resources/archivos/";
      File directorio = new File(uploadDir);
      if (!directorio.exists())
        directorio.mkdirs();
      if (archivos != null) {
        for (MultipartFile archivo : archivos) {
          if (!archivo.isEmpty()) {
            String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path rutaArchivo = Paths.get(uploadDir + nombreArchivo);
            Files.copy(archivo.getInputStream(), rutaArchivo);
            // 🧩 Crear entidad Multimedia y asociarla al Hecho
            Multimedia mm = new Multimedia();
            mm.setPath(nombreArchivo);
            mm.setTipoMultimedia(deducirTipo(Objects.requireNonNull(archivo.getContentType())));
            mm.setHecho(hecho);
            hecho.agregarMultimedia(mm);
          }
        }
      }
      // 5️⃣ Guardar hecho en BD
      repositorioHechos.save(hecho);
      return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", hecho.getId()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("error", "Error interno: " + e.getMessage()));
    }
  }
  private TipoMultimedia deducirTipo(String contentType) {
    if (contentType.startsWith("image")) return TipoMultimedia.FOTO;
    if (contentType.startsWith("video")) return TipoMultimedia.VIDEO;
    if (contentType.startsWith("audio")) return TipoMultimedia.AUDIO;
    return null;
  }
  // 🔹 Metodo privado para guardar el archivo físicamente
  private String guardarArchivoEnDisco(MultipartFile archivo) throws IOException {
    Path uploadDir = Paths.get("uploads");
    if (!Files.exists(uploadDir)) {
      Files.createDirectories(uploadDir);
    }
    String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
    Path destino = uploadDir.resolve(nombreArchivo);
    Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
    // Retorna la ruta relativa o absoluta para guardarla en BD
    return "/uploads/" + nombreArchivo;
  }

  /*
  public void publicarmeAAgregador(String URL) {

    String url = String.format("%s/fuenteDeDatos", URL);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String body = """
        {
            "URLBase": """ + URL + """
        }
    """;

    HttpEntity<String> request = new HttpEntity<>(body, headers);
    RestTemplate restTemplate = new RestTemplate();

    try {
      restTemplate.postForObject(url, request, String.class);
      System.out.println("✅ Publicado exitosamente en agregador: " + url);
    } catch (Exception e) {
      System.err.println("⚠️ No se pudo conectar al agregador en " + url);
      System.err.println("   → Error: " + e.getMessage());
    }
  }
  */
}