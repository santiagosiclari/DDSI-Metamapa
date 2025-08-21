package Agregador.persistencia;

import Agregador.business.Colecciones.Coleccion;
import Agregador.business.Colecciones.Criterio;
import Agregador.business.Colecciones.CriterioFuenteDeDatos;
import lombok.Getter;

import java.util.ArrayList;
import java.util.UUID;

public class RepositorioColecciones {
  @Getter
  public ArrayList<Coleccion> colecciones = new ArrayList<>();

  public Coleccion buscarXUUID(UUID uuid){
    return this.getColecciones().stream().filter(c -> c.getHandle().equals(uuid)).toList().get(0);
  }

/*
  public Coleccion findById(UUID id) {
    return getColecciones().stream().filter(x -> x.getHandle().equals(id)).findFirst().orElse(null);
  }

  public void update(Coleccion coleccion) {
     getColecciones().replace(coleccion.getHandle(), coleccion);
  }
*/
  /*
  public void update(Coleccion coleccionActualizada) {
    for (int i = 0; i < colecciones.size(); i++) {
      if (colecciones.get(i).getHandle().equals(coleccionActualizada.getHandle())) {
        colecciones.set(i, coleccionActualizada);
        return;
      }
    }
    */

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