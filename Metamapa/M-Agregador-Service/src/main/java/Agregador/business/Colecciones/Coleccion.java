package Agregador.business.Colecciones;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import Agregador.business.Agregador.Agregador;
import Agregador.business.Consenso.*;
import Agregador.business.Hechos.Hecho;

public class Coleccion {
    @Getter @Setter
    private String titulo;
    @Getter @Setter
    private String descripcion;
    @Getter @Setter
    private ArrayList<Criterio> criterioPertenencia;
    @Getter @Setter
    private ArrayList<Criterio> criterioNoPertenencia;
    @Getter @Setter
    private UUID handle;
    @Getter @Setter
    private Consenso consenso;
    @Getter @Setter
    private ModosDeNavegacion modoNavegacion;

    private final Agregador agregador = Agregador.getInstance();

    public Coleccion(String titulo, String desc, Consenso consenso, ArrayList<Criterio> pertenencia, ArrayList<Criterio> noPertenencia){
        this.titulo=titulo;
        this.descripcion = desc;
        this.consenso = consenso;
        this.criterioPertenencia = pertenencia;
        this.criterioNoPertenencia = noPertenencia;
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

    public ArrayList<Hecho> filtrarPorCriterios(ArrayList<Hecho> hechos, ArrayList<Criterio> criterioPertenenciaAdicional, ArrayList<Criterio> criterioNoPertenenciaAdicional, ModosDeNavegacion modoDeNavegacion) {
        //ArrayList<Hecho> hechos = agregador.getListaDeHechos();

        ArrayList<Criterio> criteriosPertenenciaCombinados = new ArrayList<Criterio>(this.getCriterioPertenencia());
        if (!criterioPertenenciaAdicional.isEmpty())
            criteriosPertenenciaCombinados.addAll(criterioPertenenciaAdicional);

       ArrayList<Criterio> criteriosNoPertenenciaCombinados = new ArrayList<Criterio>(this.getCriterioNoPertenencia());
        if (!criterioNoPertenenciaAdicional.isEmpty())
            criteriosNoPertenenciaCombinados.addAll(criterioNoPertenenciaAdicional);

        hechos = hechos.stream()
            .filter(h -> criteriosPertenenciaCombinados.stream().allMatch(c -> c.cumple(h)))
            .filter(h -> criteriosNoPertenenciaCombinados.stream().noneMatch(c -> c.cumple(h))).collect(Collectors.toCollection(ArrayList::new));

       // if(modoDeNavegacion == ModosDeNavegacion.IRRESTRICTA)
            return hechos;
        //return curarHechos(hechos);
    }

    public ArrayList<Hecho> getHechos(ArrayList<Hecho> hechos, ModosDeNavegacion modo){
        ArrayList<Hecho> hechosFiltrados = filtrarPorCriterios(hechos,new ArrayList<Criterio>(), new ArrayList<Criterio>(),modo);
        if(modo == ModosDeNavegacion.IRRESTRICTA) return hechosFiltrados;

        return curarHechos(hechosFiltrados);
    }

    public ArrayList<Hecho> curarHechos(ArrayList<Hecho> hechos){
        return hechos.stream().filter(h -> consenso.esConsensuado(h)).collect(Collectors.toCollection(ArrayList::new));
    }
}