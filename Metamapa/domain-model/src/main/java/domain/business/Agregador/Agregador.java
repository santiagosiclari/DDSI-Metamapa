package domain.business.Agregador;
import domain.business.FuentesDeDatos.FuenteDeDatos;
import java.util.ArrayList;
import lombok.Getter;
import domain.business.incidencias.Hecho;

public class Agregador {
    static public int contadorID = 1;
    @Getter
    public int id;
    @Getter
    private ArrayList<FuenteDeDatos> fuentesDeDatos;

    @Getter
    private ArrayList<Hecho> listaDeHechos;

    public void actualizarHechos() {
        ArrayList<Hecho> hechos = new ArrayList<>();
        fuentesDeDatos.forEach(f -> hechos.addAll(f.getHechos()));
        listaDeHechos = hechos;
    }

    public Agregador() {
        this.id = contadorID++;
        this.fuentesDeDatos= new ArrayList<>();
        this.listaDeHechos= new ArrayList<>();


//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//        Runnable tarea = () -> this.actualizarHechos();
//
//        scheduler.scheduleAtFixedRate(tarea, 0, 2, TimeUnit.HOURS);
        }

    public void agregarFuenteDeDatos(FuenteDeDatos fuente){
        this.fuentesDeDatos.add(fuente);
        this.actualizarHechos();
    }

    public void eliminarFuenteDeDatos(FuenteDeDatos fuente){
        this.fuentesDeDatos.remove(fuente);
        this.actualizarHechos();
    }

}
