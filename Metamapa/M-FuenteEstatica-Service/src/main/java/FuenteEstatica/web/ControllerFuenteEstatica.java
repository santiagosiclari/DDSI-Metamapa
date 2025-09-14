package FuenteEstatica.web;

import FuenteEstatica.persistencia.*;
import FuenteEstatica.business.FuentesDeDatos.*;
import FuenteEstatica.business.Hechos.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@RestController
@RequestMapping("/api-fuentesDeDatos")
public class ControllerFuenteEstatica {
  public RepositorioFuentes repositorioFuentes = new RepositorioFuentes();
  public RepositorioHechos repositorioHechos = new RepositorioHechos();
  public ControllerFuenteEstatica() {}

  //TODO no hardcodear las rutas
  String rutaPending = "Metamapa/M-FuenteEstatica-Service/src/main/resources/pendientes";
  String rutaProcessed = "Metamapa/M-FuenteEstatica-Service/src/main/resources/procesados";

  // Obtener todas las fuentes
  @GetMapping("/")
  public ArrayList<FuenteEstatica> getFuenteDeDatos() {
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
      return ResponseEntity.ok(fuenteEstatica);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }

  public ArrayList<Hecho> procesarCSVs(Integer fuenteID){
    File directorio = new File(rutaPending);
    FuenteEstatica fuente = repositorioFuentes.buscarFuente(fuenteID);;
    if (directorio.exists() && directorio.isDirectory()) {
      File[] archivos = directorio.listFiles();
      try {
        for (File archivo : archivos) {
          fuente.cargar("CSV", rutaPending + "\\" + fuente.nombre + "\\" + archivo.getName());
          Files.move(archivo.toPath(), Path.of(rutaProcessed + "\\" + fuente.nombre), StandardCopyOption.REPLACE_EXISTING);
        }
      }
      catch (Exception e){
        System.out.println("Error al procesar los archivos CSV: " + e.getMessage());
      }
    } else {
      return new ArrayList<Hecho>();
    }
    return fuente.hechos;
  }

  // este me parece que no se usa, ya que el agregador se actualiza solo por ahi esta para otra cosa
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ResponseEntity<ArrayList<Hecho>> getHechosFuenteDeDatos(@PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    ArrayList<Hecho> hechos = new ArrayList<Hecho>();
    try{
      hechos = procesarCSVs(idfuenteDeDatos);
    }
    catch (Exception e){
      return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(null);
    }
    return ResponseEntity.ok(hechos);
  }

  // Todos los hechos de todas las fuentes
  @GetMapping("/hechos")
  public ResponseEntity<ArrayList<Hecho>> getHechos(){
    ArrayList<Hecho> hechos = new ArrayList<Hecho>();
    try{
      hechos = repositorioFuentes.fuentesDeDatos.stream().map(f -> procesarCSVs(f.getId())).flatMap(ArrayList::stream).collect(Collectors.toCollection(ArrayList::new));
    }
    catch (Exception e){
      return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(null);
    }
    return ResponseEntity.ok(hechos);
  }

  // Subir un csv al file server... (ACA NO SE PROCESARIA LOS HECHOS, SOLO SE SUBE)
  @PostMapping(value = "/{idFuenteDeDatos}/cargarCSV", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
  public ResponseEntity<?> cargarCSV(@PathVariable(value = "idFuenteDeDatos") Integer idFuenteDeDatos, @RequestParam("file") MultipartFile file) {
    try { // En postman probar con form-data y file
      Path carpetaPendientes = Paths.get(rutaPending + "\\" + idFuenteDeDatos);
      Files.copy(file.getInputStream(), carpetaPendientes.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
      return ResponseEntity.ok("CSV cargado correctamente");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }

  public void publicarmeAAgregador()
  {
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