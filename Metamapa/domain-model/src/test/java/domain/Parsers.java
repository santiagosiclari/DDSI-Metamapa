/*
package domain.business.Parsers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import domain.business.incidencias.Hecho;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

class CSVHechoParserTest {

    @Test
    void parsearArchivoCSV_conLineaValida_devuelveLista() throws Exception {
        Path tempFile = Files.createTempFile("test", ".csv");
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write("titulo,desc,categoria,0.0,0.0,10/07/2025\n");
        }
        CSVHechoParser parser = new CSVHechoParser();
        List<Hecho> hechos = parser.parsearHechos(tempFile.toString(), 1);
        assertFalse(hechos.isEmpty());
        assertEquals("titulo", hechos.get(0).getTitulo());
        assertEquals("categoria", hechos.get(0).getCategoria());
    }
}

 */