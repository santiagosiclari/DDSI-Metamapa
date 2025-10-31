package Estadistica.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import Estadistica.persistencia.*;
import Estadistica.business.Estadistica.Hecho;
import Estadistica.business.Estadistica.Coleccion;
import Estadistica.DTO.HechoGeoDTO;

@Service
public class ServiceEstadistica {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final GeocodingService geocodingService; // Servicio de geocodificación
    private final RepositorioHechos repositorioHechos;
    private final RepositorioSolicitudesEliminacion repositorioSolicitudesEliminacion;
    private final RepositorioColecciones repositorioColecciones;

    public ServiceEstadistica(RestTemplate restTemplate,
                            @Value("${M.Agregador.Service.url}") String baseUrl, GeocodingService geocodingService,
                            RepositorioHechos repositorioHechos,
                            RepositorioSolicitudesEliminacion repositorioSolicitudesEliminacion,
                            RepositorioColecciones repositorioColecciones) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.geocodingService = geocodingService;
        this.repositorioHechos = repositorioHechos;
        this.repositorioSolicitudesEliminacion = repositorioSolicitudesEliminacion;
        this.repositorioColecciones = repositorioColecciones;
    }

    public void actualizar(){
        //cronjob
    }

    public void actualizarDashboards(){

    }

/*
    public Integer horaMasReportada(String categoria) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/hechos")
                .queryParam("categoria", categoria)
                .toUriString();
        return restTemplate.getForObject(url, Integer.class);
    }

    public String obtenerCategoriaMasReportada(){
        String url = baseUrl + "/api-hechos/categoria-mas-reportada";
        return restTemplate.getForObject(url, String.class);
    }

    public long getSolicitudesSpam() {
        String url = baseUrl + "/api-solicitudes/solicitudesEliminacion?spam=true";
        List<?> lista = restTemplate.getForObject(url, List.class);
        return (lista == null) ? 0L : lista.size();
    }

    public String provinciaMasReportadaDeColeccion(UUID idColeccion){
        String url = baseUrl + "/api-colecciones/" + idColeccion; //corregir eso, va a ser algo como: private static final String HECHOS_GEO_ENDPOINT = "/{id}/hechos-geo";
        HechoGeoDTO[] hechosArray = restTemplate.getForObject(
            url,
            HechoGeoDTO[].class,
            idColeccion
        );

        if (hechosArray == null || hechosArray.length == 0) {
            return null; // 204 No Content
        }

        List<HechoGeoDTO> hechos = Arrays.asList(hechosArray);
        // 2. Agrupación y Conteo por Provincia
        Map<String, Long> conteoPorProvincia = hechos.stream()
            // Llamamos al GeocodingService por cada hecho
            .collect(Collectors.groupingBy(
                h -> geocodingService.obtenerProvincia(h.getLatitud(), h.getLongitud()),
                Collectors.counting()
            ));

        // 3. Encontrar la provincia con el mayor conteo
        return conteoPorProvincia.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null); // No debería pasar si hechos no está vacío
    }

    public String provinciaMasReportada(String categoria){
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/hechos/provincia-mas-reportada")
            .queryParam("categoria", categoria)
            .toUriString();
        return restTemplate.getForObject(url, String.class);
    }*/

    public String exportarCsv(){
        StringBuilder csv = new StringBuilder();

        Map<String, String> topCategoriaStats = estadisticaCategoriaMasReportada();
        String categoriaBase = topCategoriaStats.getOrDefault("categoria", "N/A");

        // 💡 CONDICIÓN DE NO EXPORTAR: Si la categoría base es N/A, no hay datos válidos para filtrar.
        if ("N/A".equals(categoriaBase) || categoriaBase.isBlank()) {
            return ""; // Devolvemos String vacío para que el Controller retorne 204 No Content
        }

        // UUID de ejemplo (Asumimos que el Administrador definiría esto, pero para la exportación genérica, usamos un placeholder)
        UUID coleccionEjemplo = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Map<String, String> spamStats = estadisticaSpam();
        Map<String, String> horaStats = estadisticaHoraCategoria(categoriaBase);
        Map<String, String> provinciaCategoriaStats = estadisticaProvinciaCategoria(categoriaBase);
        Map<String, String> coleccionStats = estadisticaColeccionProvincia(coleccionEjemplo);

        // Formato Analítico Simple: Tipo_de_Estadistica,Clave,Valor
        csv.append("Tipo_de_Estadistica,Clave,Valor\n");

        // I. ¿Cuántas solicitudes de eliminación son spam?
        csv.append("RESUMEN_SPAM,Total_Solicitudes,")
                .append(spamStats.getOrDefault("total", "0")).append("\n");
        csv.append("RESUMEN_SPAM,Solicitudes_Spam,")
                .append(spamStats.getOrDefault("spam", "0")).append("\n");

        // II. ¿Cuál es la categoría con mayor cantidad de hechos reportados?
        csv.append("CATEGORIA_MAS_REPORTADA,Categoria_Ganadora,")
                .append(topCategoriaStats.getOrDefault("categoria", "N/A")).append("\n");

        // III. ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
        csv.append("HORA_PICO_POR_CATEGORIA,Categoria_Base,")
                .append(categoriaBase).append("\n");
        csv.append("HORA_PICO_POR_CATEGORIA,Hora_Mas_Frecuente,")
                .append(horaStats.getOrDefault("hora", "N/A")).append("\n");

        // IV. ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
        csv.append("PROVINCIA_PICO_POR_CATEGORIA,Categoria_Base,")
                .append(categoriaBase).append("\n");
        csv.append("PROVINCIA_PICO_POR_CATEGORIA,Provincia_Mas_Frecuente,")
                .append(provinciaCategoriaStats.getOrDefault("provincia", "N/A")).append("\n");

        // V. De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?
        csv.append("PROVINCIA_PICO_POR_COLECCION,ID_Coleccion_Analizada,")
                .append(coleccionEjemplo.toString()).append("\n");
        csv.append("PROVINCIA_PICO_POR_COLECCION,Provincia_Mas_Frecuente,")
                .append(coleccionStats.getOrDefault("provincia", "N/A")).append("\n");

        return csv.toString();
    }

    //¿Cuántas solicitudes de eliminación son spam?
    public Map<String,String> estadisticaSpam(){
        Map<String,String> estadisticaSpam = new HashMap<>();
        long total = repositorioSolicitudesEliminacion.count();
        long spam = repositorioSolicitudesEliminacion.countBySpamTrue();
        estadisticaSpam.put("total", String.valueOf(total));
        estadisticaSpam.put("spam", String.valueOf(spam));
    return estadisticaSpam;
    }

    //¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
    public Map<String,String> estadisticaHoraCategoria(String categoria){
        Map<String,String> estadisticaHoraCategoria = new HashMap<>();
        String hora = repositorioHechos.findByCategoria(categoria)
                .stream().filter(h -> getHora(h.getFechaHecho()) != null)
                .collect(Collectors.groupingBy(h->getHora(h.getFechaHecho()), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("N/A");
        estadisticaHoraCategoria.put("categoria", categoria);
        estadisticaHoraCategoria.put("hora", hora);
        return estadisticaHoraCategoria;
    }
    public String getHora(LocalDateTime fechayhora){
        return fechayhora.format(DateTimeFormatter.ofPattern("HH"));
    }

    //¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
    public Map<String,String> estadisticaProvinciaCategoria(String categoria){
        Map<String,String> estadisticaProvinciaCategoria = new HashMap<>();
        String provincia = repositorioHechos.findByCategoriaIgnoreCase(categoria)
                .stream().filter(h -> getProvincia(h.getLatitud(),h.getLongitud()) != null)
                .collect(Collectors.groupingBy(h -> getProvincia(h.getLatitud(),h.getLongitud()), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey())
                .orElse("N/A");
        estadisticaProvinciaCategoria.put("categoria", categoria);
        estadisticaProvinciaCategoria.put("provincia", provincia);
        return estadisticaProvinciaCategoria;
    }

    //De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?
    public Map<String,String> estadisticaColeccionProvincia(UUID idColeccion) {
        Map<String, String> estadisticaProvinciaCategoria = new HashMap<>();
        Coleccion coleccion = repositorioColecciones.findById(idColeccion).orElse(null);
        String provincia = repositorioHechos.filtrarPorCriterios(coleccion.getCriterios())
                .stream().filter(h -> (getProvincia(h.getLatitud(),h.getLongitud()) != null))
                .collect(Collectors.groupingBy(h -> getProvincia(h.getLatitud(),h.getLongitud()), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey())
                .orElse("N/A");
        estadisticaProvinciaCategoria.put("coleccion", idColeccion.toString());
        estadisticaProvinciaCategoria.put("provincia", provincia);
        return estadisticaProvinciaCategoria;
    }
    public String getProvincia(Float latitud, Float longitud){
        return geocodingService.obtenerProvincia(latitud,longitud);
    }

    //¿Cuál es la categoría con mayor cantidad de hechos reportados?
    public Map<String, String> estadisticaCategoriaMasReportada() {
        Map<String, Long> conteos = repositorioHechos.findAll().stream()
                .collect(Collectors.groupingBy(
                        h -> Optional.ofNullable(h.getCategoria()).orElse("Sin categoría"),
                        Collectors.counting()
                ));

        var top = conteos.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        Map<String, String> estadisticaCategoriaMasReportada = new HashMap<>();
        estadisticaCategoriaMasReportada.put("categoria", top.map(Map.Entry::getKey).orElse("N/A"));
        estadisticaCategoriaMasReportada.put("cantidad", top.map(e -> e.getValue().toString()).orElse("0"));
        return estadisticaCategoriaMasReportada;
    }
}