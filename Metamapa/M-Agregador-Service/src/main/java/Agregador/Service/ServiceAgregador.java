package Agregador.Service;

import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.RepositorioHechos;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

// Agregador/Service/ServiceAgregador.java
@Service
public class ServiceAgregador {

  private final ServiceFuenteDeDatos fuentes;        // orquesta requests a estática/dinámica/proxy
  private final RepositorioHechos repo;
  private final Normalizador normalizador;           // tu clase mejorada

  public ServiceAgregador(ServiceFuenteDeDatos fuentes,
                          RepositorioHechos repo,
                          Normalizador normalizador) {
    this.fuentes = fuentes;
    this.repo = repo;
    this.normalizador = normalizador;
  }


}
