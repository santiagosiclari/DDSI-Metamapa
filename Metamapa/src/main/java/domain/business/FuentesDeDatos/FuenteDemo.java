package domain.business.FuentesDeDatos;

import domain.business.incidencias.Hecho;
import domain.business.externo.demo.Conexion;

import java.net.URL;
import domain.business.FuentesDeDatos.FuenteDeDatos;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class FuenteDemo extends FuenteDeDatos {

    @Getter
    String nombre;

    @Getter
    LinkedList<Hecho> listaHecho;

    private final Conexion conexion;
    private final URL endpoint;
    private LocalDateTime ultimaConsulta;

    public FuenteDemo(Conexion conexion, URL endpoint) {
        this.conexion = conexion;
        this.endpoint = endpoint;
        this.ultimaConsulta = LocalDateTime.now().minusHours(1); // inicialización segura
    }

    public List<Hecho> obtenerHechosActualizados() {
        List<Hecho> nuevosHechos = new ArrayList<>();
        Map<String, Object> datos;

        while ((datos = conexion.siguienteHecho(endpoint, ultimaConsulta)) != null) {
            nuevosHechos.add(parsearHecho(datos));
        }

        ultimaConsulta = LocalDateTime.now();
        return nuevosHechos;
    }

    private Hecho parsearHecho(Map<String, Object> datos) {
        return new Hecho(
                (String) datos.get("id"),
                (String) datos.get("descripcion"),
                (String) datos.get("categoria"),
                (Float) datos.get("latitud"),
                (Float) datos.get("longitud"),
                (LocalDate) datos.get("fecha")
        );
    }
}
