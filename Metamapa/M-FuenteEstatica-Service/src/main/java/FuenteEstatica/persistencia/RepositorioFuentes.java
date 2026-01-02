package FuenteEstatica.persistencia;
import FuenteEstatica.business.FuentesDeDatos.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;


@Repository
public class RepositorioFuentes {
  @Value("${rutas.pendientes}")
  private String rutaPending;
  @Value("${rutas.procesados}")
  private String rutaProcessed;

  public void agregarFuente(FuenteEstatica fuente) {
    crearCarpetaSiNoExiste(rutaPending, fuente.getFuenteId());
    crearCarpetaSiNoExiste(rutaProcessed, fuente.getFuenteId());

    // Guardamos el nombre para que no se pierda al reiniciar
    guardarNombreEnDisco(fuente);
  }

  public List<FuenteEstatica> obtenerPendientes() {
    return leerFuentesDesdeRuta(rutaPending);
  }

  public List<FuenteEstatica> obtenerProcesados() {
    return leerFuentesDesdeRuta(rutaProcessed);
  }


  private List<FuenteEstatica> leerFuentesDesdeRuta(String rutaBase) {
    List<FuenteEstatica> fuentes = new ArrayList<>();
    Path basePath = Paths.get(rutaBase);

    if (!Files.exists(basePath) || !Files.isDirectory(basePath)) return fuentes;

    try (Stream<Path> carpetas = Files.list(basePath)) {
      carpetas.filter(Files::isDirectory).forEach(carpetaFuente -> {
        FuenteEstatica fuente = construirFuenteDesdeCarpeta(carpetaFuente);
        // QUITAMOS EL FILTRO DE !getHechos().isEmpty()
        if (fuente != null) {
          fuentes.add(fuente);
        }
      });
    } catch (IOException e) {
      throw new RuntimeException("Error leyendo ruta " + rutaBase, e);
    }
    return fuentes;
  }

  private Integer generarNuevoId() {
    return getFuentesDeDatos().stream()
            .mapToInt(FuenteEstatica::getFuenteId)
            .max()
            .orElse(20000) + 1;
  }

  private void guardarNombreEnDisco(FuenteEstatica fuente) {
    Path pathNombre = Paths.get(rutaPending, fuente.getFuenteId().toString(), ".nombre");
    try {
      Files.writeString(pathNombre, fuente.getNombre());
    } catch (IOException e) {
    }
  }

  private FuenteEstatica construirFuenteDesdeCarpeta(Path carpetaFuente) {
    try {
      Integer fuenteId = Integer.parseInt(carpetaFuente.getFileName().toString());

      FuenteEstatica fuente = new FuenteEstatica();
      fuente.setFuenteId(fuenteId);
      fuente.setNombre("Fuente " + fuenteId);
      fuente.setHechos(new ArrayList<>());

      try (Stream<Path> archivos = Files.list(carpetaFuente)) {
        archivos
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".csv"))
                .forEach(csv -> fuente.cargar("CSV", csv.toString()));
      }

      return fuente;

    } catch (NumberFormatException e) {
      return null;
    } catch (IOException e) {
      throw new RuntimeException("Error leyendo fuente " + carpetaFuente, e);
    }
  }

  public void marcarComoProcesada(Integer fuenteId) {
    Path origen = Paths.get(rutaPending, fuenteId.toString());
    Path destino = Paths.get(rutaProcessed, fuenteId.toString());

    if (!Files.exists(origen) || !Files.isDirectory(origen)) {
      throw new RuntimeException("La fuente " + fuenteId + " no existe en pendientes");
    }

    try {
      if (!Files.exists(destino)) {
        Files.createDirectories(destino.getParent());
        Files.move(origen, destino);
        return;
      }

      try (Stream<Path> archivos = Files.list(origen)) {
        archivos
                .filter(Files::isRegularFile)
                .forEach(archivoOrigen -> {
                  Path archivoDestino = destino.resolve(archivoOrigen.getFileName());

                  try {
                    // NO sobrescribir
                    if (!Files.exists(archivoDestino)) {
                      Files.move(archivoOrigen, archivoDestino);
                    }
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                });
      }

    } catch (IOException e) {
      throw new RuntimeException("Error al procesar fuente " + fuenteId, e);
    }
  }

  public RepositorioFuentes() {
  }

  public FuenteEstatica buscarFuente(Integer id) {

    FuenteEstatica fuente = buscarFuenteEnRuta(id, rutaPending);
    if (fuente != null) {
      return fuente;
    }

    fuente = buscarFuenteEnRuta(id, rutaProcessed);
    if (fuente != null) {
      return fuente;
    }

    throw new NoSuchElementException("No existe la fuente con ID " + id);
  }


  private FuenteEstatica buscarFuenteEnRuta(Integer id, String rutaBase) {
    Path carpeta = Paths.get(rutaBase, id.toString());

    if (!Files.exists(carpeta) || !Files.isDirectory(carpeta)) {
      return null;
    }

    return construirFuenteDesdeCarpeta(carpeta);
  }


  public List<FuenteEstatica> getFuentesDeDatos() {
    Map<Integer, FuenteEstatica> resultado = new HashMap<>();

    obtenerPendientes().forEach(f -> resultado.put(f.getFuenteId(), f));
    obtenerProcesados().forEach(f -> resultado.putIfAbsent(f.getFuenteId(), f));

    return new ArrayList<>(resultado.values());
  }

  private void crearCarpetaSiNoExiste(String rutaBase, Integer id) {
    Path carpeta = Paths.get(rutaBase, id.toString());

    try {
      if (!Files.exists(carpeta)) {
        Files.createDirectories(carpeta);
      }
    } catch (IOException e) {
      throw new RuntimeException("No se pudo crear carpeta para fuente " + id, e);
    }
  }

  private Path obtenerCarpetaFuente(Integer fuenteId) {
    Path pendiente = Paths.get(rutaPending, fuenteId.toString());
    if (Files.exists(pendiente)) {
      return pendiente;
    }

    Path procesado = Paths.get(rutaProcessed, fuenteId.toString());
    if (Files.exists(procesado)) {
      return procesado;
    }

    throw new RuntimeException("No existe la fuente con ID " + fuenteId);
  }

  public void subirArchivoCsv(MultipartFile file, Integer fuenteId) {
    Path carpetaDestino = obtenerCarpetaFuente(fuenteId);

    try {
      Files.createDirectories(carpetaDestino);

      Path destino = carpetaDestino.resolve(
              Objects.requireNonNull(file.getOriginalFilename())
      );

      try (InputStream in = file.getInputStream()) {
        Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
      }

    } catch (IOException e) {
      throw new RuntimeException("Error subiendo CSV a la fuente " + fuenteId, e);
    }
  }

}
