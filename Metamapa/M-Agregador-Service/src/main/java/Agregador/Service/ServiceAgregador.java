package Agregador.Service;

import Agregador.business.Hechos.Hecho;
import Agregador.persistencia.RepositorioHechos;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

  /** Llamado por @Scheduled y/o ControllerAgregador */
  public void actualizarHechos() {
    var batch = new ArrayList<Hecho>();

    // 1) listar todas las fuentes (dinámica/estática/proxy)
    var todas = fuentes.obtenerFuentesDeDatos();

    // 2) acumular hechos nuevos de cada fuente
    for (var f : todas) {
      batch.addAll(fuentes.getHechosNuevosDeFuente(f.getId())); // usa tu méto do ya hecho
    }

    // 3) normalizar + dedupe
    var curados = normalizador.normalizarYUnificar(batch);

    // 4) persistir
    repo.saveAll(curados);
  }
}
