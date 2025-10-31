package FuenteEstatica.business.Parsers;
import FuenteEstatica.business.Hechos.Hecho;
import FuenteEstatica.business.FuentesDeDatos.FuenteEstatica;
import com.opencsv.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CSVHechoParser implements HechoParser {
    @Override
    public ArrayList<Hecho> parsearHechos(String path, FuenteEstatica fuente) {
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
                LocalDateTime fechaHecho = LocalDate.parse(campos[5].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();

                Hecho hecho = new Hecho(titulo,descripcion, categoria, latitud, longitud, fechaHecho, fuente.getFuenteId());
                listaHecho.add(hecho);
            }
        } catch (Exception e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        }
        return listaHecho;
    }

    public ArrayList<Hecho> parsearHechos(InputStream in, FuenteEstatica fuente) {
        ArrayList<Hecho> listaHecho = new ArrayList<>();
        CSVParser parser = new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] campos = parser.parseLine(linea);
                if (campos.length < 6) {
                    System.out.println("Línea con formato inválido: " + linea);
                    continue;
                }
                String titulo = campos[0].trim();
                String descripcion = campos[1].trim();
                String categoria = campos[2].trim();
                Float latitud = Float.parseFloat(campos[3].trim());
                Float longitud = Float.parseFloat(campos[4].trim());
                LocalDateTime fechaHecho = LocalDate.parse(campos[5].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();

                Hecho hecho = new Hecho(titulo,descripcion, categoria, latitud, longitud, fechaHecho, fuente.getFuenteId());
                listaHecho.add(hecho);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo CSV", e);
        }
        return listaHecho;
    }

    public Hecho parse(String[] campos, FuenteEstatica fuente) {
        String titulo = campos[0];
        String descripcion = campos[1];
        String categoria = campos[2];
        float latitud = Float.parseFloat(campos[3]);
        float longitud = Float.parseFloat(campos[4]);
        LocalDateTime fecha = LocalDate.parse(campos[5]).atStartOfDay();
        return new Hecho(titulo, descripcion, categoria, latitud, longitud, fecha, fuente.getFuenteId());
    }
}