package domain.Persistencia;

import domain.business.incidencias.Hecho;
import java.util.ArrayList;
import lombok.Getter;

public class RepositorioHechos {
  @Getter
  public ArrayList<Hecho> hechos= new ArrayList<>();

  public void persistirHecho(Hecho h){
    hechos.add(h);
  }

  public void removerHecho(int h){
    hechos.remove(buscarHecho(h));
  }

  public Hecho buscarHecho(int id){
      return hechos.stream().filter(h -> h.getId() == id).findFirst().orElse(null);
  }

}
