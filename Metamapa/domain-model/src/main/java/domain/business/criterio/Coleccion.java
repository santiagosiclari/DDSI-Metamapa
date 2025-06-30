package domain.business.criterio;

import lombok.Getter;

import domain.business.Agregador.Agregador;
import domain.business.incidencias.Hecho;
import java.util.*;
//import org.apache.commons.lang
import java.util.UUID;
import java.util.stream.Collectors;

public class Coleccion {
    @Getter
    private String titulo;
    @Getter
    private String descripcion;
    @Getter
    private ArrayList<Criterio> criterioPertenencia;
    @Getter
    private ArrayList<Criterio> criterioNoPertenencia;
    @Getter
    private Agregador agregador;
    @Getter
    private String handle;

    public Coleccion(String titulo, String desc,ArrayList<Criterio> pertenencia,ArrayList<Criterio> noPertenencia,Agregador agregador){
        this.titulo=titulo;
        this.descripcion = desc;
        this.criterioPertenencia = pertenencia;
        this.criterioNoPertenencia = noPertenencia;
        this.agregador=agregador;
        this.handle = String.valueOf(UUID.randomUUID());
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

    public ArrayList<Hecho> filtrarPorCriterios(ArrayList<Criterio> criterioPertenenciaAdicional, ArrayList<Criterio> criterioNoPertenenciaAdicional) {
       ArrayList<Hecho> hechos = agregador.getListaDeHechos();

        ArrayList<Criterio> criteriosPertenenciaCombinados = new ArrayList<Criterio>(this.getCriterioPertenencia());
        if (!criterioPertenenciaAdicional.isEmpty())
            criteriosPertenenciaCombinados.addAll(criterioPertenenciaAdicional);

       ArrayList<Criterio> criteriosNoPertenenciaCombinados = new ArrayList<Criterio>(this.getCriterioNoPertenencia());
        if (!criterioNoPertenenciaAdicional.isEmpty())
            criteriosNoPertenenciaCombinados.addAll(criterioNoPertenenciaAdicional);

        return hechos.stream()
            .filter(h -> criteriosPertenenciaCombinados.stream().allMatch(c -> c.cumple(h)))
            .filter(h -> criteriosNoPertenenciaCombinados.stream().noneMatch(c -> c.cumple(h))).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Hecho> getHechos()
    {
        return filtrarPorCriterios(new ArrayList<Criterio>(),new ArrayList<Criterio>());
    }

}