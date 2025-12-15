package Estadistica.business.Colecciones;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Entity
@Getter @Setter
public class Coleccion {
    @Id
    private UUID handle;
    private String titulo;
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Criterio> criterios = new ArrayList<>();

    public Coleccion(String titulo, String desc, ArrayList<Criterio> criterios) {
        this.titulo = titulo;
        this.descripcion = desc;
        this.criterios = criterios;
        this.handle = UUID.randomUUID();
    }

    public Coleccion() {}

    public void agregarCriterio(Criterio criterio){
        this.criterios.add(criterio);
    }
    public void eliminarCriterio(Criterio criterio){
        this.criterios.remove(criterio);
    }

    /*public ArrayList<Hecho> filtrarPorCriterios(ArrayList<Hecho> hechos, ArrayList<Criterio> criterioPertenenciaAdicional, ArrayList<Criterio> criterioNoPertenenciaAdicional, ModosDeNavegacion modoDeNavegacion) {
        hechos = hechos.stream()
            .filter(h -> criterios.stream().allMatch(c -> c.cumple(h))).collect(Collectors.toCollection(ArrayList::new));

       // if(modoDeNavegacion == ModosDeNavegacion.IRRESTRICTA)
            return hechos;
        //return curarHechos(hechos);
    }*/

    /*public ArrayList<Hecho> getHechos(ArrayList<Hecho> hechos, ModosDeNavegacion modo){
        ArrayList<Hecho> hechosFiltrados = filtrarPorCriterios(hechos,new ArrayList<Criterio>(), new ArrayList<Criterio>(),modo);
        if(modo == ModosDeNavegacion.IRRESTRICTA) return hechosFiltrados;

        return curarHechos(hechosFiltrados);
    }*/

//    public ArrayList<Hecho> curarHechos(ArrayList<Hecho> hechos){
//        return hechos.stream().filter(h -> consenso.esConsensuado(h)).collect(Collectors.toCollection(ArrayList::new));
//    }
}