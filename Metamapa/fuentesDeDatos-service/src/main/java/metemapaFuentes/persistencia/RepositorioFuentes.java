package metemapaFuentes.persistencia;

import domain.business.FuentesDeDatos.FuenteDeDatos;
import domain.business.FuentesDeDatos.FuenteDinamica;
import domain.business.FuentesDeDatos.FuenteEstatica;
import domain.business.Parsers.CSVHechoParser;
import domain.business.Usuarios.Perfil;
import domain.business.Usuarios.Rol;
import domain.business.Usuarios.Usuario;
import domain.business.incidencias.Multimedia;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class RepositorioFuentes {
  @Getter
  public ArrayList<FuenteDeDatos> fuentesDeDatos = new ArrayList<>();

  public void agregarFuente(FuenteDeDatos fuente) {
    this.fuentesDeDatos.add(fuente);
  }

  public FuenteDeDatos buscarFuente(Integer id) {
    return fuentesDeDatos.stream().filter(f-> f.getId() == id).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }
  public RepositorioFuentes() {

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

    agregarFuente(fuenteDinamicaID1);

    //String path = "src/main/resources/desastres_naturales_argentina.csv";
    String path ="C:/Nacho/Facu/2025/DDS/TP-DDSI/Metamapa/fuentesDeDatos-service/src/main/resources/desastres_naturales_argentina.csv";
    CSVHechoParser parser = new CSVHechoParser();
    FuenteEstatica fuenteEstaticaID2 = new FuenteEstatica(path, parser);
    fuenteEstaticaID2.cargarCSV(path);
    agregarFuente(fuenteEstaticaID2);
    }
}


