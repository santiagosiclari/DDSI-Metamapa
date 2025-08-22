package FuenteDinamica.persistencia;

import FuenteDinamica.business.FuentesDeDatos.FuenteDinamica;
import FuenteDinamica.business.Hechos.Multimedia;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.html.parser.Parser;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFuentes {
  @Getter
  public ArrayList<FuenteDinamica> fuentesDinamicas = new ArrayList<>();

  public void agregarFuente(FuenteDinamica fuente) {
    this.fuentesDinamicas.add(fuente);
  }

  public FuenteDinamica buscarFuente(Integer id) {
    return fuentesDinamicas.stream().filter(f-> f.getFuenteId() == id).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }

//  public RepositorioFuentes() {
//
//    // Fuente Dinamica Con 1 Hecho
//    FuenteDinamica fuenteDinamica = new FuenteDinamica();
//    ArrayList<Multimedia> multi = new ArrayList<>();
//    Perfil admin01 = new Perfil("Juan", "Perez", 30);
//    Usuario admin = new Usuario("admin1@frba.utn.edu.ar", "algo", admin01, List.of(Rol.ADMINISTRADOR, Rol.CONTRIBUYENTE, Rol.VISUALIZADOR));
//    fuenteDinamica.agregarHecho(
//        "Hecho demo",
//        "Esto es una descripcion demo",
//        "Metamapa/demo",
//        0f,
//        0f,
//        LocalDate.of(2025, 6, 22),
//        1,
//        false,
//        multi);
//
//    agregarFuente(fuenteDinamica);
//
///*    String path ="fuentesDeDatos-service/src/main/resources/desastres_naturales_argentina.csv";
//    //String path = "Metamapa/agregador-service/src/main/resources/desastres_naturales_argentina.csv";
//
//    CSVHechoParser parser = new CSVHechoParser();
//    FuenteEstatica fuenteEstaticaID2 = new FuenteEstatica("desastres_naturales_argentina");
//    fuenteEstaticaID2.agregarHecho(parser.parsearHechos(path,fuenteEstaticaID2.getId()));
//    //fuenteEstaticaID2.setParser(parser);
//    //fuenteEstaticaID2.cargarCSV(path);
//    agregarFuente(fuenteEstaticaID2);*/
//    }
}


