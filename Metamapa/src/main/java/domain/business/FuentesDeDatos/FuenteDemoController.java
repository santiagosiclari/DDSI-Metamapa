package domain.business.FuentesDeDatos;

import domain.business.FuentesDeDatos.FuenteDemo;
import domain.business.incidencias.Hecho;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/demo")
public class FuenteDemoController {

    private final FuenteDemo fuenteDemo;

    public FuenteDemoController(FuenteDemo fuenteDemo) {
        this.fuenteDemo = fuenteDemo;
    }

    @GetMapping("/hechos")
    public List<Hecho> hechos() {
        return fuenteDemo.obtenerHechosActualizados();
    }
}
