package metemapaFuentes.persistencia;

import domain.business.Agregador.Agregador;
import domain.business.FuentesDeDatos.FuenteDinamica;
import domain.business.FuentesDeDatos.FuenteEstatica;
import domain.business.Parsers.CSVHechoParser;
import domain.business.Usuarios.Perfil;
import domain.business.Usuarios.Rol;
import domain.business.Usuarios.Usuario;
import domain.business.incidencias.Multimedia;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class RepositorioAgregador {
  @Getter
  public ArrayList<Agregador> agregadores = new ArrayList<>();


  public void agregarAgregador(Agregador agregador) {
    this.agregadores.add(agregador);
  }
  public Agregador buscarAgregador(Integer id) {
    return agregadores.stream().filter(f-> f.getId() == id).findFirst().orElseThrow(()-> new IllegalArgumentException("No se encontro una fuente con ese ID"));
  }
  public RepositorioAgregador() {
    // Agregador con 1 fuente dinamica y una fuente estatica
    Agregador agregador1 = new Agregador();

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

    //String path = "agregador-service/src/main/resources/desastres_naturales_argentina.csv";
    String path = "Metamapa/agregador-service/src/main/resources/desastres_naturales_argentina.csv";

    CSVHechoParser parser = new CSVHechoParser();
    FuenteEstatica fuenteEstaticaID2 = new FuenteEstatica(path.toString(), parser);
    fuenteEstaticaID2.cargarCSV(path);

    agregador1.agregarFuenteDeDatos(fuenteDinamicaID1);
    agregador1.agregarFuenteDeDatos(fuenteEstaticaID2);
    agregarAgregador(agregador1);
  }
}