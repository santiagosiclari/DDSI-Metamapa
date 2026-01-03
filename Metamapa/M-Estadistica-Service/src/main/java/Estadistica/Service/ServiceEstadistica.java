package Estadistica.Service;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import Estadistica.persistencia.*;
import Estadistica.business.Colecciones.Coleccion;
import Estadistica.business.Estadistica.*;
import Estadistica.business.Hechos.Hecho;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceEstadistica {
    private final RepositorioHechos repositorioHechos;
    private final RepositorioSolicitudesEliminacion repositorioSolicitudesEliminacion;
    private final RepositorioColecciones repositorioColecciones;
    private final RepositorioEstadisticas repositorioEstadisticas;

    public ServiceEstadistica(RepositorioHechos repositorioHechos,
                              RepositorioSolicitudesEliminacion repositorioSolicitudesEliminacion,
                              RepositorioColecciones repositorioColecciones,
                              RepositorioEstadisticas repositorioEstadisticas) {
        this.repositorioHechos = repositorioHechos;
        this.repositorioSolicitudesEliminacion = repositorioSolicitudesEliminacion;
        this.repositorioColecciones = repositorioColecciones;
        this.repositorioEstadisticas = repositorioEstadisticas;
    }

    @Transactional
    public void actualizar() {
        CantidadDeSpam spamStats = estadisticaSpam();
        CategoriaConMasHechos topCategoriaStats = estadisticaCategoriaMasReportada();
        repositorioEstadisticas.save(spamStats);
        repositorioEstadisticas.save(topCategoriaStats);
        List<String> categorias = repositorioHechos.obtenerCategorias();
        for (String categoria : categorias) {
            HoraConMasHechosPorCategoria horaStats = estadisticaHoraCategoria(categoria);
            ProvinciaConMasHechosPorCategoria provinciaCategoriaStats = estadisticaProvinciaCategoria(categoria);
            repositorioEstadisticas.save(horaStats);
            repositorioEstadisticas.save(provinciaCategoriaStats);
        }
        List<Coleccion> colecciones = repositorioColecciones.findAll();
        for (Coleccion coleccion : colecciones) {
            ProvinciaConMasHechosPorColeccion coleccionStats = estadisticaColeccionProvincia(coleccion.getHandle());
            repositorioEstadisticas.save(coleccionStats);
        }
    }

    public byte[] exportarCsv() {

        StringBuilder csv = new StringBuilder();

        CategoriaConMasHechos topCategoriaStats = obtenerUltimaEstadisticaCategoriaMasReportada();
        String categoriaBase = topCategoriaStats.getCategoria();

        // CONDICIÓN DE NO EXPORTAR
        if ("N/A".equals(categoriaBase) || categoriaBase.isBlank()) {
            return new byte[0];
        }

        UUID coleccionEjemplo = UUID.fromString("00000000-0000-0000-0000-000000000001");

        CantidadDeSpam spamStats = obtenerUltimaEstadisticaSpam();
        HoraConMasHechosPorCategoria horaStats = obtenerUltimaEstadisticaHoraCategoria(categoriaBase);
        ProvinciaConMasHechosPorCategoria provinciaCategoriaStats =
                obtenerUltimaEstadisticaProvinciaCategoria(categoriaBase);
        ProvinciaConMasHechosPorColeccion coleccionStats =
                obtenerUltimaEstadisticaColeccionProvincia(coleccionEjemplo);

        // Header
        csv.append("Tipo_de_Estadistica,Clave,Valor\n");

        csv.append("RESUMEN_SPAM,Solicitudes_Spam,")
                .append(spamStats.getCantidadSolicitudesSpam()).append("\n");

        csv.append("CATEGORIA_MAS_REPORTADA,Categoria_Ganadora,")
                .append(topCategoriaStats.getCategoria()).append("\n");

        csv.append("HORA_PICO_POR_CATEGORIA,Categoria_Base,")
                .append(categoriaBase).append("\n");

        csv.append("HORA_PICO_POR_CATEGORIA,Hora_Mas_Frecuente,")
                .append(horaStats.getHora() == null ? "N/A" : horaStats.getHora())
                .append("\n");

        csv.append("PROVINCIA_PICO_POR_CATEGORIA,Categoria_Base,")
                .append(categoriaBase).append("\n");

        csv.append("PROVINCIA_PICO_POR_CATEGORIA,Provincia_Mas_Frecuente,")
                .append(provinciaCategoriaStats.getProvincia() == null
                        ? "N/A"
                        : provinciaCategoriaStats.getProvincia())
                .append("\n");

        csv.append("PROVINCIA_PICO_POR_COLECCION,ID_Coleccion_Analizada,")
                .append(coleccionEjemplo).append("\n");

        csv.append("PROVINCIA_PICO_POR_COLECCION,Provincia_Mas_Frecuente,")
                .append(coleccionStats.getProvincia()).append("\n");

        // Guardado de estadísticas
        repositorioEstadisticas.save(spamStats);
        repositorioEstadisticas.save(horaStats);
        repositorioEstadisticas.save(provinciaCategoriaStats);
        repositorioEstadisticas.save(coleccionStats);
        repositorioEstadisticas.save(topCategoriaStats);

        // ===== UTF-8 + BOM =====
        byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.writeBytes(bom);
        output.writeBytes(csv.toString().getBytes(StandardCharsets.UTF_8));

        return output.toByteArray();
    }

    //¿Cuántas solicitudes de eliminación son spam?
    public CantidadDeSpam estadisticaSpam() {
        long spam = repositorioSolicitudesEliminacion.findAllWhereEstadoIs("SPAM").size();

        return new CantidadDeSpam(spam);
    }

    //¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
    public HoraConMasHechosPorCategoria estadisticaHoraCategoria(String categoria) {
        String hora = repositorioHechos.obtenerHoraConMasHechos(categoria).orElse("N/A");
        return new HoraConMasHechosPorCategoria(hora,categoria);
    }

    //¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
    public ProvinciaConMasHechosPorCategoria estadisticaProvinciaCategoria(String categoria) {
        String provincia = repositorioHechos.obtenerProvinciaConMasHechosPorCategoria(categoria).orElse("N/A");
        return new ProvinciaConMasHechosPorCategoria(provincia,categoria);
    }

    //De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?
    public ProvinciaConMasHechosPorColeccion estadisticaColeccionProvincia(UUID idColeccion) {
        System.out.println("criterios" + repositorioColecciones.getColeccion(idColeccion).get().getCriterios().toString());
        System.out.println("hechos filtrados" + repositorioHechos.filtrarPorCriterios(repositorioColecciones.getColeccion(idColeccion).get().getCriterios()));
        Coleccion coleccion = repositorioColecciones.findById(idColeccion).orElse(null);
        List<Hecho> hechos = repositorioHechos.filtrarPorCriterios(coleccion.getCriterios());
        String provincia = repositorioHechos.filtrarPorCriterios(coleccion.getCriterios())
                .stream().filter(h -> (h.getProvincia() != null))
                .collect(Collectors.groupingBy(Hecho::getProvincia, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        return new ProvinciaConMasHechosPorColeccion(provincia,coleccion);
    }

    //¿Cuál es la categoría con mayor cantidad de hechos reportados?
    public CategoriaConMasHechos estadisticaCategoriaMasReportada() {
        String categoria = repositorioHechos.obtenerCategoriaConMasHechos().orElse("N/A");
        return new CategoriaConMasHechos(categoria);
    }

    public CantidadDeSpam obtenerUltimaEstadisticaSpam() {
        return repositorioEstadisticas.obtenerMasNueva(CantidadDeSpam.class, null).orElse(new CantidadDeSpam(0));
    }

    public CategoriaConMasHechos obtenerUltimaEstadisticaCategoriaMasReportada() {
        return repositorioEstadisticas.obtenerMasNueva(CategoriaConMasHechos.class, null).orElse(new CategoriaConMasHechos("N/A"));
    }

    public HoraConMasHechosPorCategoria obtenerUltimaEstadisticaHoraCategoria(String categoria) {
        Map map = new HashMap<String, Object>();
        map.put("categoria", categoria);
        return (HoraConMasHechosPorCategoria) repositorioEstadisticas.obtenerMasNueva(HoraConMasHechosPorCategoria.class, map)
                .orElse(new HoraConMasHechosPorCategoria("N/A", categoria));
    }

    public ProvinciaConMasHechosPorCategoria obtenerUltimaEstadisticaProvinciaCategoria(String categoria) {
        Map map = new HashMap<String, Object>();
        map.put("categoria", categoria);
        return (ProvinciaConMasHechosPorCategoria) repositorioEstadisticas.obtenerMasNueva(ProvinciaConMasHechosPorCategoria.class, map)
                .orElse(new ProvinciaConMasHechosPorCategoria("N/A", categoria));
    }

    public ProvinciaConMasHechosPorColeccion obtenerUltimaEstadisticaColeccionProvincia(UUID idColeccion) {
        Coleccion coleccion = repositorioColecciones.findById(idColeccion).orElse(null);
        Map map = new HashMap<>();
        map.put("coleccion", coleccion);
        return (ProvinciaConMasHechosPorColeccion) repositorioEstadisticas.obtenerMasNueva(ProvinciaConMasHechosPorColeccion.class, map)
                .orElse(new ProvinciaConMasHechosPorColeccion("N/A", null));
    }
}