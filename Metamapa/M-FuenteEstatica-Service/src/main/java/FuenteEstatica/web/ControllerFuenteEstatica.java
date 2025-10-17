package FuenteEstatica.web;
import FuenteEstatica.persistencia.*;
import FuenteEstatica.business.FuentesDeDatos.*;
import FuenteEstatica.business.Hechos.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteEstatica {
  @Value("${rutas.pendientes}")
  private String rutaPending;
  @Value("${rutas.procesados}")
  private String rutaProcessed;
  private final RepositorioFuentes repositorioFuentes;

  public ControllerFuenteEstatica(RepositorioFuentes repositorioFuentes) {
    this.repositorioFuentes = repositorioFuentes;
  }

  // Obtener todas las fuentes
  @GetMapping("/")
  public List<FuenteEstatica> getFuenteDeDatos() {
    return repositorioFuentes.getFuentesDeDatos();
  }

  // Obtener una fuente por id
  @GetMapping("/{idFuenteDeDatos}")
  public FuenteEstatica getFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }

  // Crear una fuente
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> crearFuenteDeDatos(@RequestBody Map<String, Object> requestBody) {
    try {
      String nombreFE = (String) requestBody.get("nombre");
      FuenteEstatica fuenteEstatica = new FuenteEstatica(nombreFE);
      repositorioFuentes.agregarFuente(fuenteEstatica);
      //crear carpeta para la fuente
      Path rutaPendienteFuente = Path.of(rutaPending, String.valueOf(fuenteEstatica.getFuenteId()));
      Files.createDirectories(rutaPendienteFuente);
      return ResponseEntity.ok(fuenteEstatica);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }

  public ArrayList<Hecho> procesarCSVs(Integer fuenteID) {
    Path rutaPendienteFuente = Path.of(rutaPending, String.valueOf(fuenteID));
    File directorio = rutaPendienteFuente.toFile();
    FuenteEstatica fuente = repositorioFuentes.buscarFuente(fuenteID);
    if (directorio.exists() && directorio.isDirectory()) {
      File[] archivos = directorio.listFiles(File::isFile); // solo archivos
      try {
        for (File archivo : archivos) {
          String rutaArchivo = archivo.getAbsolutePath();
          fuente.cargar("CSV", rutaArchivo);
          // Mover archivo a la carpeta de "procesados" de la fuente
          Path carpetaProcesados = Path.of(rutaProcessed, String.valueOf(fuenteID));
          Files.createDirectories(carpetaProcesados); // crear carpeta si no existe
          Files.move(archivo.toPath(), carpetaProcesados.resolve(archivo.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (Exception e) {
        System.out.println("Error al procesar los archivos CSV: " + e.getMessage());
      }
    }
    return fuente.hechos;
  }

  // este me parece que no se usa, ya que el agregador se actualiza solo por ahi esta para otra cosa
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ResponseEntity<ArrayList<Hecho>> getHechosFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    ArrayList<Hecho> hechos;
    try {
      hechos = procesarCSVs(idfuenteDeDatos);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(null);
    }
    return ResponseEntity.ok(hechos);
  }

  // Todos los hechos de todas las fuentes
  @GetMapping("/hechos")
  public ResponseEntity<ArrayList<Hecho>> getHechos() {
    ArrayList<Hecho> hechos;
    try {
      hechos = repositorioFuentes.fuentesDeDatos.stream().map(f -> procesarCSVs(f.getFuenteId())).flatMap(ArrayList::stream).collect(Collectors.toCollection(ArrayList::new));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(null);
    }
    return ResponseEntity.ok(hechos);
  }

  // Subir un csv al file server... (ACA NO SE PROCESARIA LOS HECHOS, SOLO SE SUBE)
  @PostMapping(value = "/{idFuenteDeDatos}/cargarCSV", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
  public ResponseEntity<?> cargarCSV(@PathVariable(value = "idFuenteDeDatos") Integer idFuenteDeDatos, @RequestParam("file") MultipartFile file) {
    try { // En postman probar con form-data y file
      Path carpetaPendientes = Paths.get(rutaPending, String.valueOf(idFuenteDeDatos));
      Files.createDirectories(carpetaPendientes); // asegurarse que exista la carpeta
      Files.copy(file.getInputStream(), carpetaPendientes.resolve(Objects.requireNonNull(file.getOriginalFilename())), StandardCopyOption.REPLACE_EXISTING);
      return ResponseEntity.ok("CSV cargado correctamente");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
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