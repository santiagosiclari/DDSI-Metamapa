package controllers;
import domain.business.criterio.*;
import domain.business.incidencias.Hecho;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class controllerColecciones {
  public static void main(String[] args) {
    //SpringApplication.run(testApplication.class, args);
    SpringApplication app = new SpringApplication(controllers.controllerColecciones.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
//    app.setDefaultProperties(Collections.singletonMap("server.address", "192.168.0.169"));
    app.run(args);
  }

  @GetMapping("/colecciones/{identificador}/hechos")
  public ArrayList<Hecho> getHechosColeccion(@PathVariable String identificador,
                                @RequestParam(value = "tituloP",required = false) String tituloP,
                                @RequestParam(value = "descripcionP",required = false) String descripcionP,
                                @RequestParam(value = "categoriaP", required = false) String categoriaP,
                                @RequestParam(value = "fecha_reporte_desdeP",required = false) String fecha_reporte_desdeP,
                                @RequestParam(value = "fecha_reporte_hastaP", required = false) String fecha_reporte_hastaP,
                                @RequestParam(value = "fecha_acontecimiento_desdeP", required = false) String fecha_acontecimiento_desdeP,
                                @RequestParam(value = "fecha_acontecimiento_hastaP", required = false) String fecha_acontecimiento_hastaP,
                                @RequestParam(value = "latitudP", required = false) String latitudP,
                                @RequestParam(value = "longitudP", required = false) String longitudP,
                                @RequestParam(value = "tipoMultimediaP",required = false) String tipoMultimediaP,
                                @RequestParam(value = "tituloNP",required = false) String tituloNP,
                                @RequestParam(value = "descripcionNP",required = false) String descripcionNP,
                                @RequestParam(value = "categoriaNP", required = false) String categoriaNP,
                                @RequestParam(value = "fecha_reporte_desdeNP",required = false) String fecha_reporte_desdeNP,
                                @RequestParam(value = "fecha_reporte_hastaNP", required = false) String fecha_reporte_hastaNP,
                                @RequestParam(value = "fecha_acontecimiento_desdeNP", required = false) String fecha_acontecimiento_desdeNP,
                                @RequestParam(value = "fecha_acontecimiento_hastaNP", required = false) String fecha_acontecimiento_hastaNP,
                                @RequestParam(value = "latitudNP", required = false) String latitudNP,
                                @RequestParam(value = "longitudNP", required = false) String longitudNP,
                                @RequestParam(value = "tipoMultimediaNP",required = false) String tipoMultimediaNP)
  {
    ArrayList<Criterio> criteriosP = new ArrayList<Criterio>();
    ArrayList<Criterio> criteriosNP = new ArrayList<Criterio>();

    if(tituloP != null)criteriosP.add(new CriterioTitulo(tituloP));
    if(descripcionP != null)criteriosNP.add(new CriterioDescripcion(descripcionP));
    if(categoriaP != null)criteriosP.add(new CriterioCategoria(categoriaP));

    if(fecha_acontecimiento_desdeP != null)criteriosP.add(new CriterioFecha(LocalDate.parse(fecha_acontecimiento_desdeP),null));
    if(fecha_acontecimiento_hastaP != null)criteriosP.add(new CriterioFecha(null,LocalDate.parse(fecha_acontecimiento_hastaP)));
    if(fecha_reporte_desdeP != null)criteriosP.add(new CriterioFecha(LocalDate.parse(fecha_reporte_desdeP),null));
    if(fecha_reporte_hastaP != null)criteriosP.add(new CriterioFecha(null,LocalDate.parse(fecha_reporte_hastaP)));
    if(latitudP != null && longitudP != null)criteriosP.add(new CriterioUbicacion(Float.parseFloat(latitudP),Float.parseFloat(longitudP)));
//    if(tipoMultimediaP != null)criteriosP.add(CriterioMultimedia())

    //TODO agregar criterios de no pertenencia a la API

    //TODO query a las colecciones cuando haya persistencia
    Coleccion coleccion = new Coleccion("prueba","dummy",null,null,null); // = query_colecciones(identificador)
    return coleccion.filtrarPorCriterios(criteriosP,criteriosNP);

  }

  //TODO: Crear una coleccion (post /colecciones)

  //TODO: Modificar algoritmo de consenso (patch /colecciones/{id})
}
