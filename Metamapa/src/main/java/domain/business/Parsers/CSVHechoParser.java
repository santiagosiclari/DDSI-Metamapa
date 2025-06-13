package domain.business.Parsers;

import domain.business.incidencias.Hecho;
import domain.business.Parsers.HechoParser;
import domain.business.incidencias.Ubicacion;

//import infrastructure.dto.HechoDTO;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;


public class CSVHechoParser implements HechoParser {
    @Override
    public ArrayList<Hecho> parsearHecho(String path) {
        ArrayList<Hecho> listaHecho = new ArrayList<Hecho>();

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .build();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // salteamos el encabezado
                }

                String[] campos = parser.parseLine(linea);

                // El formato del archivo debe tener 6 columnas
                if (campos.length < 6) {
                    System.out.println("Línea con formato inválido: " + linea);
                    continue;
                }


                String titulo = campos[0].trim();
                String descripcion = campos[1].trim();
                String categoria = campos[2].trim();
                Float latitud = Float.parseFloat(campos[3].trim());
                Float longitud = Float.parseFloat(campos[4].trim());
                LocalDate fechaHecho = LocalDate.parse(campos[5].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                Hecho hecho = new Hecho(titulo,descripcion, categoria, latitud, longitud, fechaHecho,null,null,null, new ArrayList<>());
                // TODO: revisar Deberiamos inicializar en NULL el resto de los campos del contructor del hecho???
                listaHecho.add(hecho);
            }
        } catch (Exception e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        }
        return listaHecho;
    }
}