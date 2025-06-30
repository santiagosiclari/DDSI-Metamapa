package domain.business.FuentesDeDatos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import domain.business.Parsers.HechoParser;
import java.net.URL;
import lombok.Getter;
//import infrastructure.dto.client.MetaMapaRestClient;

@JsonTypeName("FUENTEPROXY")
public abstract class FuenteProxy extends FuenteDeDatos {
    @Getter
    public URL endpointBase;
    @Getter
    public HechoParser parser;

    public FuenteProxy(URL endpointBase, HechoParser parser) {
        this.endpointBase = endpointBase;
        this.parser = parser;
        this.id = contadorID++;
        this.tipoFuente = tipoFuente.FUENTEPROXY;
    }
    /*public void actualizarHechos() {
    }*/
    /*public void getHechosDeColeccion() {
    }
    public void solicitarEliminacion() {
    }*/
}