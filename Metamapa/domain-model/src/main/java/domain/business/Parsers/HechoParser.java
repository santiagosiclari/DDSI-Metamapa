package domain.business.Parsers;

import domain.business.incidencias.Hecho;
import java.util.ArrayList;

public interface HechoParser {

    ArrayList<Hecho> parsearHechos(String path);

}
