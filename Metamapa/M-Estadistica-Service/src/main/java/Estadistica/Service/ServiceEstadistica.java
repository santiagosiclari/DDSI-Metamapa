package Estadistica.Service;

import Estadistica.DTO.EstadisticaDTO;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ServiceEstadistica {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ServiceEstadistica(RestTemplate restTemplate,
                            @Value("${M.Agregador.Service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
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
        return "";
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