package FuenteMetamapa.business.FuentesDeDatos;
import FuenteMetamapa.business.Parsers.HechoParser;
import FuenteMetamapa.business.Hechos.Hecho;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import lombok.*;
//import infrastructure.dto.client.MetaMapaRestClient;
//import jakarta.persistence.*;

@JsonTypeName("FUENTEPROXY")
@Getter @Setter
public abstract class FuenteProxy {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;
    public String endpointBase;
    public HechoParser parser;
    static protected Integer contadorID = 3000000;
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