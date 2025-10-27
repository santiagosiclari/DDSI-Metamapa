package FuenteMetamapa.business.Parsers;
import FuenteMetamapa.business.FuentesDeDatos.FuenteMetamapa;
import FuenteMetamapa.business.Hechos.Hecho;
import java.io.InputStream;
import java.util.ArrayList;

public interface HechoParser {
    ArrayList<Hecho> parsearHechos(String path, FuenteMetamapa fuenteID);

    ArrayList<Hecho> parsearHechos(InputStream in, FuenteMetamapa fuenteID);
}