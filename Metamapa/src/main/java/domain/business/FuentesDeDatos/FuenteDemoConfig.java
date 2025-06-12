package domain.business.FuentesDeDatos;

import domain.business.externo.demo.Conexion;
import domain.business.externo.demo.ConexionPrueba;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class FuenteDemoConfig {

    @Bean
    public FuenteDemo fuenteDemo() throws MalformedURLException {
        Conexion conexion = new ConexionPrueba(); // tu dummy
        URL endpoint = new URL("http://localhost/demo");
        return new FuenteDemo(conexion, endpoint);
    }
}
