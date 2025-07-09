package domain.Persistencia;
import domain.business.incidencias.Hecho;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;

public class RepositorioHechos {
  @Getter
  public ArrayList<Hecho> hechos= new ArrayList<>();

  public void persistirHecho(Hecho h){
    hechos.add(h);
  }

  public void removerHecho(Integer h){
    hechos.remove(buscarHecho(h));
  }

  public Hecho buscarHecho(Integer id){
      return hechos.stream().filter(h -> h.getId().equals(id)).findFirst().orElse(null);


      // Buscar en la lista de solicitudes usando el UUID
      /*return solicitudes.stream()
          .filter(solicitud -> solicitud.getId().equals(id))  // Compara el ID de la solicitud
          .findFirst();  // Devuelve el primer resultado, si lo encuentra
    }*/
  }

}
