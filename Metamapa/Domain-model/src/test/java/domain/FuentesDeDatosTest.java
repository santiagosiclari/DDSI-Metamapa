/*
package domain.business.FuentesDeDatos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import domain.business.Usuarios.Perfil;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class FuenteDinamicaTest {

  @Test
  void agregarHechoObjeto_incrementaLista() {
    FuenteDinamica fuente = new FuenteDinamica();
    Perfil perfil = new Perfil("Luis", "Pérez", 20);
    List<Multimedia> multimedia = new ArrayList<>();
    Hecho hecho = new Hecho("T", "D", "cat", 0f, 0f, LocalDate.now(), perfil, 1, false, multimedia);
    fuente.agregarHecho(hecho);
    assertEquals(1, fuente.getHechos().size());
    assertEquals("T", fuente.getHechos().get(0).getTitulo());
  }

  @Test
  void agregarHechoParametros_incrementaLista() {
    FuenteDinamica fuente = new FuenteDinamica();
    Perfil perfil = new Perfil("Luis", "Pérez", 30);
    List<Multimedia> multimedia = new ArrayList<>();
    fuente.agregarHecho("T", "D", "cat", 1f, 2f, LocalDate.of(2025, 7, 10), perfil, false, new ArrayList<>());
    assertEquals(1, fuente.getHechos().size());
    assertEquals("T", fuente.getHechos().get(0).getTitulo());
  }
}
*/