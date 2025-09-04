package Agregador.web;
import Agregador.DTO.CriterioDTO;
import Agregador.business.Agregador.Agregador;
import Agregador.business.Colecciones.*;
import Agregador.business.Hechos.Hecho;

import java.util.*;
import Agregador.Service.ServiceFuenteDeDatos;
import Agregador.persistencia.RepositorioAgregador;
import Agregador.persistencia.RepositorioColecciones;
import Agregador.persistencia.RepositorioHechos;
import ch.qos.logback.classic.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api-agregador")
public class ControllerAgregador {
  private final ServiceFuenteDeDatos servicefuenteDeDatos;
  private final RepositorioAgregador repositorioAgregador;
  private final RepositorioHechos repositorioHechos = new RepositorioHechos();

  public ControllerAgregador(ServiceFuenteDeDatos servicefuenteDeDatos, RepositorioColecciones repositorioColecciones, RepositorioAgregador repositorioAgregador) {
    this.servicefuenteDeDatos = servicefuenteDeDatos;
    this.repositorioAgregador = repositorioAgregador;
  }

  /*
    public void guardarHechos(int idFuente){
      ArrayList<Map<String,Object>> hechos = servicefuenteDeDatos.getHechosDeFuente(idFuente);

      hechos.forEach(h -> repositorioAgregador.persistirHechos(h));
    }
  */

  private ArrayList<String> obtenerURLFuentes() {
    ArrayList<String> URLsFuentes = new ArrayList<String>();
    URLsFuentes.add("${M.FuenteDinamica.service.url}");
    URLsFuentes.add("${M.FuenteEstatica.service.url}");
    URLsFuentes.add("${M.FuenteProxy.service.url}");
    return URLsFuentes;
  }

  public void actualizarHechos() {
    ArrayList<String> URLsFuentes = obtenerURLFuentes();
    ArrayList<Hecho> hechos = new ArrayList<>();
    URLsFuentes.forEach(url -> {
      //hechos.addAll(new ServiceFuenteDeDatos(new RestTemplate(), repositorioHechos).getHechos());
    });
    repositorioAgregador.getAgregador().actualizarHechos(hechos);
  }

  public void consensuarHechos() {
    //TODO implementar que dispare procedures en la BBDD
  }

  @GetMapping("/")
  public ResponseEntity<Agregador> getAgregador() {
    Agregador agregador = repositorioAgregador.getAgregador();
    if (agregador == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(agregador);
  }

//  @PostMapping("/api-agregador/fuentes/actualizar")
//  public ResponseEntity<Void> actualizarAgregador() {
//    var fuentes = servicefuenteDeDatos.obtenerFuenteDeDatos();
//    if (fuentes == null || fuentes.isEmpty()) return ResponseEntity.noContent().build();
//    repositorioAgregador.getAgregador().actualizarFuentesDeDatos(fuentes);
//    return ResponseEntity.noContent().build();
//  }

  // esto se va a comunicar con el servicio de colecciones
  // y las colecciones filtran estos hechos
//  @GetMapping("/hechos")
//  public ResponseEntity<ArrayList<Hecho>> getAgregadorHechos() {
//    ArrayList<Hecho> hechos = repositorioAgregador.getAgregador().getListaDeHechos();
//
//    if (hechos == null || hechos.isEmpty()) {
//      return ResponseEntity.noContent().build();
//    }
//    return ResponseEntity.ok(hechos);
//  }
}