package domain.business.criterio;
import lombok.Getter;
import domain.business.Agregador.Agregador;
import domain.business.incidencias.Hecho;
import java.util.*;
//import org.apache.commons.lang
import java.util.UUID;
import java.util.stream.Collectors;
import domain.business.Consenso.*;
import lombok.Setter;

public class Coleccion {
    @Getter @Setter
    private String titulo;
    @Getter @Setter
    private String descripcion;
    @Getter @Setter
    private ArrayList<Criterio> criterioPertenencia;
    @Getter @Setter
    private ArrayList<Criterio> criterioNoPertenencia;
    //@Getter
    //private Agregador agregador;
    @Getter
    private UUID handle;
    @Getter @Setter
    private Consenso consenso;
   // @Getter @Setter
   // private ModosDeNavegacion modoNavegacion;

    private final Agregador agregador = Agregador.getInstance();

    public Coleccion(String titulo, String desc,ArrayList<Criterio> pertenencia,ArrayList<Criterio> noPertenencia){
        this.titulo=titulo;
        this.descripcion = desc;
        this.criterioPertenencia = pertenencia;
        this.criterioNoPertenencia = noPertenencia;
        //this.agregador=agregador;
        this.handle = UUID.randomUUID();
    }

    public void agregarCriterioPertenencia(Criterio criterio){
        this.criterioPertenencia.add(criterio);
    }
    public void eliminarCriterioPertenencia(Criterio criterio){
        this.criterioPertenencia.remove(criterio);
    }

    public void agregarCriterioNoPertenencia(Criterio criterio){
        this.criterioNoPertenencia.add(criterio);
    }
    public void eliminarCriterioNoPertenencia(Criterio criterio){
        this.criterioNoPertenencia.remove(criterio);
    }

    /*publicArrayList<Hecho> filtrarPorCriterios(){
       ArrayList<Hecho> hechos = agregador.getListaDeHechos();
        return hechos.stream()
            .filter(h -> this.getCriterioPertenencia().stream().allMatch(c -> c.cumple(h)))
            .filter(h -> this.getCriterioNoPertenencia().stream().noneMatch(c -> c.cumple(h)))
            .toList();
    }
    publicArrayList<Hecho> filtrarPorCriterios(List<Criterio> criterioPertenenciaAdicional,ArrayList<Criterio> criterioNoPertenenciaAdicional){
       ArrayList<Hecho> hechos = agregador.getListaDeHechos();
        return hechos.stream()
            .filter(h -> this.getCriterioPertenencia().add(criterioPertenenciaAdicional).stream().allMatch(c -> c.cumple(h)))
            .filter(h -> this.getCriterioNoPertenencia().add(criterioNoPertenenciaAdicional).stream().noneMatch(c -> c.cumple(h)))
            .toList();
    }*/

    public ArrayList<Hecho> filtrarPorCriterios(ArrayList<Criterio> criterioPertenenciaAdicional, ArrayList<Criterio> criterioNoPertenenciaAdicional,ModosDeNavegacion modoDeNavegacion) {
        ArrayList<Hecho> hechos = agregador.getListaDeHechos();

        ArrayList<Criterio> criteriosPertenenciaCombinados = new ArrayList<Criterio>(this.getCriterioPertenencia());
        if (!criterioPertenenciaAdicional.isEmpty())
            criteriosPertenenciaCombinados.addAll(criterioPertenenciaAdicional);

       ArrayList<Criterio> criteriosNoPertenenciaCombinados = new ArrayList<Criterio>(this.getCriterioNoPertenencia());
        if (!criterioNoPertenenciaAdicional.isEmpty())
            criteriosNoPertenenciaCombinados.addAll(criterioNoPertenenciaAdicional);

        hechos = hechos.stream()
            .filter(h -> criteriosPertenenciaCombinados.stream().allMatch(c -> c.cumple(h)))
            .filter(h -> criteriosNoPertenenciaCombinados.stream().noneMatch(c -> c.cumple(h))).collect(Collectors.toCollection(ArrayList::new));

        if(modoDeNavegacion == ModosDeNavegacion.IRRESTRICTA) return hechos;
        return curarHechos(hechos);
    }

    public ArrayList<Hecho> getHechos(ModosDeNavegacion modo){
        ArrayList<Hecho> hechos = filtrarPorCriterios(new ArrayList<Criterio>(), new ArrayList<Criterio>(),modo);
        if(modo == ModosDeNavegacion.IRRESTRICTA) return hechos;

        return curarHechos(hechos);
    }

    public ArrayList<Hecho> curarHechos(ArrayList<Hecho> hechos)
    {
        return hechos.stream().filter(h -> consenso.esConsensuado(h, agregador.getFuentesDeDatos())).collect(Collectors.toCollection(ArrayList::new));
    }
}