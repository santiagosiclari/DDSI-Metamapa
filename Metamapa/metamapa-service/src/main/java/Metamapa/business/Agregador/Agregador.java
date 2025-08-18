package Metamapa.business.Agregador;
import Metamapa.business.FuentesDeDatos.FuenteDeDatos;
import Metamapa.business.Hechos.Hecho;
import java.util.ArrayList;
import lombok.Getter;

public class Agregador {
    private static Agregador agregador = null;
    @Getter
    public ArrayList<FuenteDeDatos> fuentesDeDatos;
    @Getter
    public ArrayList<Hecho> listaDeHechos;

    public void actualizarHechos() {
        ArrayList<Hecho> hechos = new ArrayList<>();
        fuentesDeDatos.forEach(f -> hechos.addAll(f.getHechos()));
        listaDeHechos = hechos;
    }

    private Agregador() {
        this.fuentesDeDatos= new ArrayList<>();
        this.listaDeHechos= new ArrayList<>();


//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//        Runnable tarea = () -> this.actualizarHechos();
//
//        scheduler.scheduleAtFixedRate(tarea, 0, 2, TimeUnit.HOURS);
        }
        //Instancio el agregador como singleton
        public static Agregador getInstance() {
        if (agregador == null)
            agregador = new Agregador();
        return agregador;
        }

    public void agregarFuenteDeDatos(FuenteDeDatos fuente){
        if (!fuentesDeDatos.contains(fuente)){
            fuentesDeDatos.add(fuente);
            this.actualizarHechos();
        }
    }

    public void actualizarFuentesDeDatos(ArrayList<FuenteDeDatos> fuentesDeDatos){
        this.fuentesDeDatos = fuentesDeDatos;
    }

    public void removerFuenteDeDatos(Integer idFuente){
        this.fuentesDeDatos.removeIf(f -> f.getId() == idFuente);
        this.actualizarHechos();
    }

}
