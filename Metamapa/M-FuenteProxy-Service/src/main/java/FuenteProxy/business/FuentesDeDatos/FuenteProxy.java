package FuenteProxy.business.FuentesDeDatos;
import FuenteProxy.business.Parsers.HechoParser;
import FuenteProxy.business.Hechos.Hecho;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
//import infrastructure.dto.client.MetaMapaRestClient;

@JsonTypeName("FUENTEPROXY")
@Getter
public abstract class FuenteProxy {
    public String endpointBase;
    @Setter
    public HechoParser parser;
    static protected Integer contadorID = 3000000;
    protected Integer id;
    public String nombre;
    public ArrayList<Hecho> hechos;
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