package metemapaFuentes.web;
import DTO.HechoDTO;
import domain.business.FuentesDeDatos.*;
import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import domain.business.incidencias.TipoMultimedia;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import metemapaFuentes.persistencia.RepositorioFuentes;
import metemapaFuentes.service.ServiceIncidencias;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api-fuentesDeDatos")

public class ControllerFuentesDeDatos {

  private final ServiceIncidencias serviceIncidencias;
  public RepositorioFuentes repositorioFuentes = new RepositorioFuentes();

  public ControllerFuentesDeDatos(ServiceIncidencias serviceIncidencias){
    this.serviceIncidencias= serviceIncidencias;
  }



  @GetMapping("/{idFuenteDeDatos}")
  public FuenteDeDatos getFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }

  //TODO este me parece que no se usa, ya que el agregador se actualiza solo
  //TODO por ahi esta para otra cosa
  @GetMapping("/{idFuenteDeDatos}/hechos")
  public ArrayList<Hecho> getHechosFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return serviceIncidencias.obtenerHechosXIDFuente(idfuenteDeDatos);
  }

  @PostMapping (value = "/{idFuenteDeDatos}/cargarHecho", consumes = "application/json", produces = "application/json")
  public ResponseEntity cargarHecho(@PathVariable(value = "idFuenteDeDatos") Integer idFuenteDeDatos, @RequestBody Map<String, Object> requestBody) {
    try{
      if (!repositorioFuentes.buscarFuente(idFuenteDeDatos).getTipoFuente().equals(TipoFuente.FUENTEDINAMICA)) {
        return ResponseEntity
            .badRequest()
            .body(Map.of("error", "Sólo se puede cargar un hecho manual en fuentes dinámicas"));
      }

      String titulo = (String) requestBody.get("titulo");
      String descripcion = (String) requestBody.get("descripcion");
      String categoria = (String) requestBody.get("categoria");
      //Float latitud = (Float) requestBody.get("latitud");
      //Float longitud = (Float) requestBody.get("longitud");
      //LocalDate fechaHecho = (LocalDate) requestBody.get("fechaHecho");


      Number latNum = (Number) requestBody.get("latitud");
      Float latitud = latNum != null ? latNum.floatValue() : null;
      Number lonNum = (Number) requestBody.get("longitud");
      Float longitud = lonNum != null ? lonNum.floatValue() : null;

      String fechaStr = (String) requestBody.get("fechaHecho");
      LocalDate fechaHecho = (fechaStr != null && !fechaStr.isBlank())
          ? LocalDate.parse(fechaStr)
          : null;

      Perfil autor = null;
      //Perfil autor = (Perfil) requestBody.get("autor");
      //Boolean anonimo = (Boolean) requestBody.get("anonimo");
      //ArrayList<Multimedia> multimedia = null;
      //ArrayList<Multimedia> multimedia = (ArrayList<Multimedia>) requestBody.get("multimedia");

      Boolean anonimo = false;
      if (requestBody.get("anonimo") instanceof Boolean) {
        anonimo = (Boolean) requestBody.get("anonimo");
      }
      @SuppressWarnings("unchecked")
      List<Map<String,Object>> mmMaps = (List<Map<String,Object>>) requestBody.get("multimedia");
      List<Multimedia> multimedia = mmMaps != null
          ? mmMaps.stream().map(m -> {
        // extraigo tipo y ruta
        String tipoStr = (String) m.get("tipoMultimedia");
        String path    = (String) m.get("path");
        // convierto la cadena al enum
        TipoMultimedia tipo = TipoMultimedia.valueOf(tipoStr);
        return new Multimedia(tipo, path);
      }).collect(Collectors.toList())
          : new ArrayList<>();

      Hecho hecho = new Hecho(
          titulo,
          descripcion,
          categoria,
          latitud,
          longitud,
          fechaHecho,
          autor,
          idFuenteDeDatos,
          anonimo,
          multimedia);

      repositorioFuentes.buscarFuente(idFuenteDeDatos).getHechos().add(hecho);
      return ResponseEntity.ok(Map.of(
          "id", hecho.getId(),
          "message", "Hecho cargado correctamente con id" + hecho.getId()
      ));

    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error interno: " + e.getMessage()));
    }
  }


  @PostMapping (value = "/{idFuenteDeDatos}/cargarCSV", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
  public ResponseEntity cargarCSV(
      @PathVariable(value = "idFuenteDeDatos") Integer idFuenteDeDatos,
      @RequestParam("file") MultipartFile file) {
    try{
      if (!repositorioFuentes.buscarFuente(idFuenteDeDatos).getTipoFuente().equals(TipoFuente.FUENTEESTATICA)) {
        return ResponseEntity
            .badRequest()
            .body("Sólo se puede cargar CSV en fuentes estáticas");
      }
      //TODO repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream()).forEach(h -> repositorioHechos.agregar(h)); para tratar directamente con el repositorio de hechos en vez de con las fuentes
      repositorioFuentes.buscarFuente(idFuenteDeDatos).agregarHecho(repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream(),idFuenteDeDatos));
      return ResponseEntity.ok(repositorioFuentes.getParserCSV().parsearHechos(file.getInputStream(),idFuenteDeDatos));



    }catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }
  @GetMapping("/")
  public ArrayList<FuenteDeDatos> getFuenteDeDatos(){
    return repositorioFuentes.getFuentesDeDatos();
  }


  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity crearFuenteDeDatos(@RequestBody Map<String, Object> requestBody) {
    try {
      switch (requestBody.get("tipo").toString()) {
        case "FuenteEstatica":
          String nombreFE = (String) requestBody.get("nombre");
          FuenteEstatica fuenteEstatica = new FuenteEstatica(nombreFE);
          repositorioFuentes.agregarFuente(fuenteEstatica);
          return ResponseEntity.ok(fuenteEstatica);
        case "FuenteDemo":
          String nombreFD = (String) requestBody.get("nombre");
          String urlFD = (String) requestBody.get("url");
          FuenteDemo fuenteDemo = new FuenteDemo(nombreFD, urlFD);
          repositorioFuentes.agregarFuente(fuenteDemo);
          return ResponseEntity.ok(fuenteDemo);
        case "FuenteMetamapa":
          String nombreFM = (String) requestBody.get("nombre");
          String urlFM = (String) requestBody.get("url");
          FuenteMetamapa fuenteMetamapa = new FuenteMetamapa(nombreFM, urlFM);
          repositorioFuentes.agregarFuente(fuenteMetamapa);
          return ResponseEntity.ok(fuenteMetamapa);
        default:
          // tipo no reconocido -> 400 Bad Request
          return ResponseEntity
              .badRequest()
              .body("Tipo de fuente inválido: " + requestBody.get("tipo").toString());
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno " + e.getMessage());
    }
  }

}