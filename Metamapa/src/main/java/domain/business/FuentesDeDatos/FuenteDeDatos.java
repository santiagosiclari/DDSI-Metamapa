package domain.business.FuentesDeDatos;

import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import domain.business.incidencias.TipoMultimedia;
import domain.business.incidencias.Ubicacion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import lombok.Getter;
import org.javatuples.Pair;


public abstract class FuenteDeDatos {

  @Getter
  public String nombre;

  @Getter
/*  LinkedList<Hecho> listaHecho;*/
  public ArrayList<Hecho> hechos;

   void agregarHecho(String titulo, String descripcion, String categoria, Float latitud, Float longitud, LocalDate fechaHecho, FuenteDeDatos fuenteDeDatos, Perfil autor, Boolean anonimo, Boolean eliminado, ArrayList<Pair<TipoMultimedia,String>> multimedia)
   {
   }
}


/*  void agregarHechosParser(ArrayList<HechoDTO> hechos){
    hechos.stream().map(h->agregarHecho(h.getTitulo(), h.getDescripcion(), h.getCategoria(), h.getUbicacion().getLatitud(), h.getUbicacion().getLongitud(), h.getFechaHecho(),))
  }
}*/

