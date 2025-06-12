package domain.business.FuentesDeDatos;

import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

//TODO: CAMBIAR A ABSTRACTA EN DIAGRAMA?
public abstract class FuenteDeDatos {

  @Getter
  String nombre;

  @Getter
/*  LinkedList<Hecho> listaHecho;*/
  List<Hecho> hecho;

  void agregarHecho(){};
}