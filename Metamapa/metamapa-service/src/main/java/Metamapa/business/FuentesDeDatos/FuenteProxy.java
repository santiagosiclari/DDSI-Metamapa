package Metamapa.business.FuentesDeDatos;
import Metamapa.business.Parsers.HechoParser;
import Metamapa.business.Hechos.Hecho;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
//import infrastructure.dto.client.MetaMapaRestClient;

@JsonTypeName("FUENTEPROXY")
public abstract class FuenteProxy {
    @Getter
    public String endpointBase;
    @Getter @Setter
    public HechoParser parser;
    @Getter
    static protected Integer contadorID = 3000000;
    @Getter
    protected Integer id;
    @Getter
    public String nombre;
    @Getter
    public ArrayList<Hecho> hechos;
    @Getter
    public TipoFuente tipoFuente;

    public FuenteProxy(String nombre, String endpointBase) {
        if (contadorID > 3999999) {
            throw new RuntimeException("No hay mas espacio para nuevas Fuentes Proxy:(");
        } else {
            this.nombre = nombre;
            this.endpointBase = endpointBase;
            this.id = contadorID++;
        }
    }
}