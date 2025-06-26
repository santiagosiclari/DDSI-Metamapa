package controllers;

import domain.business.FuentesDeDatos.FuenteDinamica;
import domain.business.Usuarios.Perfil;
import domain.business.Usuarios.Rol;
import domain.business.Usuarios.Usuario;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import domain.business.incidencias.Ubicacion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class controllerFuentesDeDatos {
  public static void main(String[] args) {
    //SpringApplication.run(testApplication.class, args);
    SpringApplication app = new SpringApplication(controllers.controllerFuentesDeDatos.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "9001"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    var context = app.run(args);
    // para cerrar la app, comentar cuando se prueben cosas
    context.close();
  }

  @GetMapping("/getHechos")
  public ArrayList<Hecho> getHechos(@RequestParam(value = "fuenteDeDatos",required = false) String fuenteDeDatos, @RequestParam(value = "autor",required = false) String autor) {

    Perfil admin01 = new Perfil("Juan", "Perez", 30);
    Usuario admin = new Usuario("admin1@frba.utn.edu.ar", "algo", admin01, List.of(Rol.ADMINISTRADOR, Rol.CONTRIBUYENTE));
    FuenteDinamica fuente = new FuenteDinamica();
    fuente.agregarHecho("Hecho demo",
        "Esto es una descripcion demo",
        "demo", 0f, 0f, LocalDate.of(2025, 6, 22), admin01, false, new ArrayList<Multimedia>());

    ArrayList<Hecho> hechos = fuente.getHechos();
    return hechos;
  }

 /* @GetMapping("/fuentesDeDatos/{idFuenteDeDatos}/hechos")
  public ArrayList<Hecho>
  getHechosFuente(
      @PathVariable(value = "idFuenteDeDatos") Integer idfuenteDeDatos)
  {
    ArrayList<Hecho> hechos = getFuenteDeDatosXID(idfuenteDeDatos);
  return hechos;
  }*/

  }
