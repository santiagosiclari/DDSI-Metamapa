/*
package domain.business.criterio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import domain.business.Agregador.Agregador;
import domain.business.incidencias.Hecho;
import domain.business.Consenso.Consenso;
import domain.business.Consenso.ModosDeNavegacion;
import domain.business.Usuarios.Perfil;
import domain.business.Usuarios.Usuario;
import domain.business.Usuarios.Rol;
import domain.business.FuentesDeDatos.FuenteDinamica;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

class ColeccionTest {

    private final Agregador agregador = Agregador.getInstance();
    private Hecho hechoA;
    private Hecho hechoB;

    @BeforeEach
    void setUp() {
        agregador.getListaDeHechos().clear();
        hechoA = new Hecho("A", "DescA", "catA", 0f, 0f, LocalDate.now(), null, 1, false, new ArrayList<>());
        hechoB = new Hecho("B", "DescB", "catB", 0f, 0f, LocalDate.now(), null, 1, false, new ArrayList<>());
        agregador.getListaDeHechos().addAll(List.of(hechoA, hechoB));
        agregador.setFuentesDeDatos(new ArrayList<>());
    }

    @Test
    void modoIrrestricto_aplicaCriterioCategoria() {
        CriterioCategoria criterio = new CriterioCategoria("catA");
        Coleccion coleccion = new Coleccion("Test", "desc", new ArrayList<>(List.of(criterio)), new ArrayList<>());
        List<Hecho> resultado = coleccion.getHechos(ModosDeNavegacion.IRRESTRICTA);
        assertEquals(1, resultado.size());
        assertEquals("catA", resultado.get(0).getCategoria());
    }

    @Test
    void modoCurado_filtraConConsenso() {
        Coleccion coleccion = new Coleccion("Test", "desc", new ArrayList<>(), new ArrayList<>());
        coleccion.setConsenso((h, f) -> h.getTitulo().equals("A"));
        List<Hecho> resultado = coleccion.getHechos(ModosDeNavegacion.CURADA);
        assertEquals(1, resultado.size());
        assertEquals("A", resultado.get(0).getTitulo());
    }

    @Test
    void administradorAsociaAlgoritmoDeConsenso_seDebeGuardarConsenso() {
        Perfil perfil = new Perfil("Lucía", "Martínez", 35);
        Usuario admin = new Usuario("admin@frba.utn.edu.ar", "secreta", perfil, List.of(Rol.ADMINISTRADOR));
        assertTrue(admin.tieneRol(Rol.ADMINISTRADOR));

        // Preparar fuente y agregador
        agregador.setFuentesDeDatos(new ArrayList<>());
        FuenteDinamica fuente = new FuenteDinamica();
        fuente.agregarHecho("Inundación", "", "clima", 1f, 1f,
            LocalDate.of(2023, 4, 5), perfil, false, new ArrayList<>());
        fuente.agregarHecho("Sequía", "", "clima", 2f, 2f,
            LocalDate.of(2023, 5, 10), perfil, false, new ArrayList<>());
        agregador.getFuentesDeDatos().add(fuente);

        Coleccion coleccion = new Coleccion("Eventos Climáticos", "Fenómenos extremos",
            new ArrayList<>(), new ArrayList<>());
        Consenso algoritmo = Consenso.stringToConsenso("Absoluto");
        coleccion.setConsenso(algoritmo);

        assertNotNull(coleccion.getConsenso());
        assertEquals(algoritmo, coleccion.getConsenso());
    }

    @Test
    void visualizadorOContribuyentePuedeElegirModoDeNavegacion() {
        Perfil perfil = new Perfil("Emi", "Siclari", 25);
        Usuario visualizador = new Usuario("emi@frba.utn.edu.ar", "clave", perfil, List.of(Rol.VISUALIZADOR));
        Usuario contribuyente = new Usuario("colab@frba.utn.edu.ar", "clave", perfil, List.of(Rol.CONTRIBUYENTE));
        assertTrue(visualizador.tieneRol(Rol.VISUALIZADOR));
        assertTrue(contribuyente.tieneRol(Rol.CONTRIBUYENTE));

        // Preparar fuente y agregador
        agregador.setFuentesDeDatos(new ArrayList<>());
        FuenteDinamica fuente = new FuenteDinamica();
        fuente.agregarHecho("Derrame Petrolero", "", "medioambiente", 1f, 1f,
            LocalDate.of(2023, 3, 20), perfil, false, new ArrayList<>());
        fuente.agregarHecho("Incendio Forestal", "", "medioambiente", 2f, 2f,
            LocalDate.of(2023, 4, 5), perfil, false, new ArrayList<>());
        agregador.getFuentesDeDatos().add(fuente);

        Coleccion coleccion = new Coleccion("Catástrofes Naturales", "Eventos ambientales",
            new ArrayList<>(), new ArrayList<>());
        coleccion.setConsenso((h, f) -> h.getTitulo().contains("Incendio"));

        List<Hecho> hechosModoIrrestricto = coleccion.getHechos(ModosDeNavegacion.IRRESTRICTA);
        List<Hecho> hechosModoCurado = coleccion.getHechos(ModosDeNavegacion.CURADA);

        assertEquals(2, hechosModoIrrestricto.size());
        assertEquals(1, hechosModoCurado.size());
        assertTrue(hechosModoCurado.get(0).getTitulo().contains("Incendio"));
    }
}
*/







