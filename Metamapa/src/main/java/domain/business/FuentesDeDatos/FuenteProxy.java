package domain.business.FuentesDeDatos;

import domain.business.Parsers.HechoParser;
import java.net.URL;
import lombok.Getter;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import domain.business.incidencias.Hecho;
//import infrastructure.dto.client.MetaMapaRestClient;
import domain.business.criterio.Criterio;
import domain.business.criterio.Coleccion;
import java.time.LocalDate;

public abstract class FuenteProxy extends FuenteDeDatos {
    @Getter
    public URL endpointBase;
    @Getter
    public HechoParser parser;

    public FuenteProxy(URL endpointBase, HechoParser parser) {
        this.endpointBase = endpointBase;
        this.parser = parser;
        this.id = contadorID++;
    }
    /*public void actualizarHechos() {
    }*/
    /*public void getHechosDeColeccion() {
    }
    public void solicitarEliminacion() {
    }*/
}