package metemapaFuentes.web;


import domain.business.FuentesDeDatos.FuenteDeDatos;
import metemapaFuentes.persistencia.RepositorioFuentes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fuenteDeDatos")

public class ControllerFuentesDeDatos {

  public RepositorioFuentes repositorioFuentes = new RepositorioFuentes();

  @GetMapping("/{idFuenteDeDatos}/hechos")
  public FuenteDeDatos getFuenteDeDatos(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos) {
    return repositorioFuentes.buscarFuente(idfuenteDeDatos);
  }

  }
