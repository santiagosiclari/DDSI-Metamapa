package Agregador.Service;
import Agregador.business.Hechos.Hecho;
import org.springframework.stereotype.Service;
import Agregador.business.Consenso.*;
import java.util.ArrayList;
import Agregador.persistencia.*;

@Service
public class ServiceConsenso {
  RepositorioConsenso repositorioConsenso;
  RepositorioHechos repositorioHechos;

  public ServiceConsenso(RepositorioConsenso repositorioConsenso, RepositorioHechos repositorioHechos) {
    this.repositorioConsenso = repositorioConsenso;
    this.repositorioHechos = repositorioHechos;
  }

  public void consensuarHechos() {
    ArrayList<Hecho> hechos = (ArrayList<Hecho>) repositorioHechos.findAll();
    ArrayList<Consenso> consensos = (ArrayList<Consenso>) repositorioConsenso.findAll();

    hechos.forEach(h -> consensos.forEach(c -> {
      if (c.esConsensuado(h, hechos)) {
        h.agregarConsenso(c);
        System.out.println("Conseso: " + c.toString());
      }
    }));

    //todo persistir hechos
  }
}