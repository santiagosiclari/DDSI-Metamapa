package Agregador.business.Agregador;
import java.util.ArrayList;
import lombok.Getter;
import Agregador.business.Hechos.*;
import Agregador.business.Solicitudes.*;

public class Agregador {
    private static Agregador agregador = null;
    @Getter
    public ArrayList<Hecho> listaHechos;
    @Getter
    public ArrayList<SolicitudEliminacion> listaSolicitudesEliminacion;
    @Getter
    public ArrayList<SolicitudEdicion> listaSolicitudesEdicion;

    private Agregador() {
        this.listaHechos= new ArrayList<>();
        this.listaSolicitudesEliminacion = new ArrayList<>();
        this.listaSolicitudesEdicion = new ArrayList<>();
        }

        //Instancio el agregador como singleton
        public static Agregador getInstance() {
        if (agregador == null)
            agregador = new Agregador();
        return agregador;
        }
        public SolicitudEliminacion findSolicitudById(Integer id){
            return listaSolicitudesEliminacion.get(id);
        }

        public void actualizarHechos(ArrayList<Hecho> hechos)
        {
            //TODO implementar la actualizacion de hechos
        }
}
