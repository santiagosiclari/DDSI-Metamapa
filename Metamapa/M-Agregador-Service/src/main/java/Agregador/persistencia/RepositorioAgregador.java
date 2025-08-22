package Agregador.persistencia;
import Agregador.business.Agregador.Agregador;
import Agregador.business.Solicitudes.SolicitudEliminacion;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RepositorioAgregador {
  @Getter
  public  Agregador agregador;

  public RepositorioAgregador() {
    this.agregador = Agregador.getInstance();
  }

  // ==== Solicitudes ====
  public SolicitudEliminacion findSolicitudById(Integer id) {
    return agregador.findSolicitudById(id);
  }
  //public void saveSolicitud(SolicitudEliminacion s) {
  //  agregador.saveSolicitud(s);
  //}

/*
  public AgregadorDTO getAgregadorDTO(){
    AgregadorDTO agregadorDTO = AgregadorDTO.getInstance();
    agregadorDTO.setFuentesDeDatos(this.agregador.getFuentesDeDatos());
    agregadorDTO.setListaDeHechos(this.agregador.getListaDeHechos());
    return agregadorDTO;
  }
  */

  //TODO el agregador ya tiene una lista de fuentesDTO y HechosDTO,
  //TODO no hace falta lo demas. y desde aca no se persiste nada, se llama al
  //TODO servicio de fuentes de datos para una fuente de datos y al de hechos para un hecho.


  /*
public
  ArrayList<FuenteDeDatosDTO> fuentes = new ArrayList<FuenteDeDatosDTO>();
  ArrayList<Map<String,Object>> hechos = new ArrayList<Map<String,Object>>();

  public ArrayList<FuenteDeDatosDTO> getFuentes()
  {
      return fuentes;
  }

  public void persistirHechos(Map<String,Object> hecho)
  {
      hechos.add(hecho);
  }



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