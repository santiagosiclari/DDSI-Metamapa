package domain.business.FuentesDeDatos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import domain.business.Parsers.HechoParser;
import java.net.URL;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
//import infrastructure.dto.client.MetaMapaRestClient;

@JsonTypeName("FUENTEPROXY")
public abstract class FuenteProxy extends FuenteDeDatos {
    @Getter
    public String endpointBase;
    @Getter @Setter
    public HechoParser parser;

    public FuenteProxy(String nombre,String endpointBase) {
        this.nombre = nombre;
        this.endpointBase = endpointBase;
        this.id =contadorID++;
        this.tipoFuente = tipoFuente.FUENTEPROXY;
    }

    public FuenteProxy() {

    }
    /*public void actualizarHechos() {
    }*/
    /*public void getHechosDeColeccion() {
    }
    public void solicitarEliminacion() {
    }*/
}