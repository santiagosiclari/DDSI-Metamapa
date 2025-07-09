package metemapaAgregador.persistencia;
import domain.business.Agregador.Agregador;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;
public class RepositorioAgregador {
  @Getter @Setter
  public  Agregador agregador;

  public RepositorioAgregador() {
    this.agregador = Agregador.getInstance();
  }

  ArrayList<Integer> fuentes = new ArrayList<Integer>();
  ArrayList<Map<String,Object>> hechos = new ArrayList<Map<String,Object>>();

  public ArrayList<Integer> getFuentes()
  {
      return fuentes;
  }

  public void persistirHechos(Map<String,Object> hecho)
  {
      hechos.add(hecho);
  }
    /*


    // Fuente Dinamica Con 1 Hecho
    FuenteDinamica fuenteDinamicaID1 = new FuenteDinamica();
    Perfil admin01 = new Perfil("Juan", "Perez", 30);
    Usuario admin = new Usuario("admin1@frba.utn.edu.ar", "algo", admin01, List.of(Rol.ADMINISTRADOR, Rol.CONTRIBUYENTE, Rol.VISUALIZADOR));
    fuenteDinamicaID1.agregarHecho(
        "Hecho demo",
        "Esto es una descripcion demo",
        "Metamapa/demo",
        0f,
        0f,
        LocalDate.of(2025, 6, 22),
        admin01,
        false,
        new ArrayList<Multimedia>());

    String path = "agregador-service/src/main/resources/desastres_naturales_argentina.csv";
    //String path = "Metamapa/agregador-service/src/main/resources/desastres_naturales_argentina.csv";

    CSVHechoParser parser = new CSVHechoParser();
    FuenteEstatica fuenteEstaticaID2 = new FuenteEstatica(path.toString(), parser);
    fuenteEstaticaID2.cargarCSV(path);

    agregador.agregarFuenteDeDatos(fuenteDinamicaID1);
    agregador.agregarFuenteDeDatos(fuenteEstaticaID2);
  }*/
}