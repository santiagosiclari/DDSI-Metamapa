package FuenteEstatica.business.Parsers;

import FuenteEstatica.business.Hechos.Hecho;
import java.io.InputStream;
import java.util.ArrayList;

public interface HechoParser {

    ArrayList<Hecho> parsearHechos(String path, Integer fuenteID);

    ArrayList<Hecho> parsearHechos(InputStream in, Integer fuenteID);
}
