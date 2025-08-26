package Agregador.persistencia;

import Agregador.business.Colecciones.*;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class RepositorioColecciones {
  @Getter
  public ArrayList<Coleccion> colecciones = new ArrayList<>();

  public Optional<Coleccion> buscarXUUID(UUID uuid){
    return this.getColecciones().stream()
            .filter(c -> c.getHandle().equals(uuid))
            .findFirst();
  }

  public void update(Coleccion coleccion) {
     getColecciones().removeIf(c -> c.getHandle().equals(coleccion.getHandle()));
      getColecciones().add(coleccion);
  }

  public boolean eliminar(UUID id) {
    return colecciones.removeIf(c -> c.getHandle().equals(id));
  }
  public boolean contiene(UUID id) {
    return colecciones.stream().anyMatch(c -> c.getHandle().equals(id));
  }

/*  public RepositorioColecciones(){
    ArrayList<Criterio> criteriosPColeccionTest = new ArrayList<>();
    ArrayList<Criterio> criteriosNPColeccionTest = new ArrayList<>();
    CriterioFuenteDeDatos criterioFDD = new CriterioFuenteDeDatos(1); //ID de la fuente de datos dinamica
    CriterioFuenteDeDatos criterioFDE = new CriterioFuenteDeDatos(2); //ID de la fuente de datos estatica
    criteriosPColeccionTest.add(criterioFDD);
    criteriosNPColeccionTest.add(criterioFDE);
    Coleccion coleccionTest = new Coleccion("coleccionTest","Esta es una coleccion test",criteriosPColeccionTest,criteriosNPColeccionTest);
    this.getColecciones().add(coleccionTest);
    //TODO crear una coleccion y agregarla a la lista
  }*/
}