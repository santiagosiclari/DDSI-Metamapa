package Estadistica.Service;

import Estadistica.DTO.EstadisticaDTO;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import Estadistica.Service.GeocodingService;
import Estadistica.DTO.HechoGeoDTO;

@Service
public class ServiceEstadistica {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final GeocodingService geocodingService; // Servicio de geocodificación

    public ServiceEstadistica(RestTemplate restTemplate,
                            @Value("${M.Agregador.Service.url}") String baseUrl, GeocodingService geocodingService) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.geocodingService = geocodingService;
    }

    public void actualizar(){
        //cronjob
    }

    public void actualizarDashboards(){

    }

    //TODO: cambiar para que el procesamiento de datos se haga en estadistica, no en agregador
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
    }

    public String exportarCsv(){
        return "";
    }

    public EstadisticaDTO obtenerResumen() {
        EstadisticaDTO dto = new EstadisticaDTO();

        //TODO: falta resto de cosas
        dto.setProvinciaMasReportada(null);
        dto.setCantidadProvinciaMasReportada(0);
        dto.setProvinciaPorCategoria(null);
        dto.setCantidadProvinciaPorCategoria(0);
        dto.setHechosPorHoraCategoria(null);

        //Categoría más reportada
        String topCategoria = obtenerCategoriaMasReportada();
        dto.setCategoriaMasReportada(topCategoria);

        dto.setCantidadCategoriaMasReportada(0);

        // 2) Cantidad de solicitudes de eliminación que son spam
        long spamCount = getSolicitudesSpam();
        dto.setCantidadSolicitudesSpam(spamCount);

        return dto;
    }
}