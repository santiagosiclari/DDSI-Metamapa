package Estadistica.Service;

import Estadistica.business.*;
import Estadistica.persistencia.RepositoroHechos;
import Estadistica.web.ControllerEstadistica;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

@Service
@RequiredArgsConstructor
   public class TareasProgramadas {
     private final ControllerEstadistica controllerEstadistica;
     private final RepositoroHechos repositoroHechos;
     private final MeterRegistry registry;
     private final RestTemplate restTemplate = new RestTemplate();


     private Hecho JsonToHecho(Map<String, Object> json) {
       Categoria categoria = new Categoria((String)json.get("categoria"));
       //todo pasar de long y lat a provincia
       Provincia provincia = new Provincia((String)json.get("provincia"));
       Hora hora = new Hora((LocalTime)json.get("hora"));
       //todo ver como mandar esto, si directamente el bool o la soli entera
       List<SolicitudEliminacion> solicitudesEliminacion = ((List<Boolean>)json.get("solicitudesEliminacion")).stream().map(s -> new SolicitudEliminacion(s)).toList();
       List<Coleccion> colecciones = ((List<String>)json.get("solicitudesEliminacion")).stream().map(c -> new Coleccion(c)).toList();

       return new Hecho(provincia,categoria,colecciones,solicitudesEliminacion,hora);
     }

     public void actualizarEstadisticas(String UrlHechos,String UrlSolEliminacion) {
       //Hechos
       ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
               UrlHechos, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
       );
       List<Map<String, Object>> raw = Optional.ofNullable(resp.getBody()).orElseGet(List::of);
       // imprimir raw para debug
       //System.out.println("Hechos raw de la fuente " + urlBase + ": " + raw);
       List<Hecho> hechos = raw.stream()
               .map(json -> JsonToHecho(json))
               .toList();
       repositoroHechos.saveAll(hechos);

       //por ahi no hace falta hacer el de solicitudes y lo podemos hacer all junto en uno
     }


     @Scheduled(fixedRate = 30 * 60 * 1000) //TODO: revisar tiempo
     public void actualizarEstadisticaUI() {
       var counter = registry.find("http.server.requests.count").counter();
       double requests = (counter != null) ? counter.count() : 0.0;
       if (requests < 100) {
         controllerEstadistica.actualizarEstadisticaUI();
       }
     }
}