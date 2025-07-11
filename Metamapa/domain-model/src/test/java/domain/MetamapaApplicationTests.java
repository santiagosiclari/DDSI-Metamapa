package domain;/*
package domain;
import domain.business.FuentesDeDatos.FuenteDeDatos;
import domain.business.FuentesDeDatos.FuenteEstatica;
import domain.business.Parsers.CSVHechoParser;
import domain.business.Usuarios.Perfil;
import domain.business.Usuarios.Usuario;
import domain.business.criterio.Coleccion;
import domain.business.criterio.CriterioCategoria;
import domain.business.criterio.CriterioFecha;
import domain.business.tiposSolicitudes.EstadoSolicitud;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import domain.business.FuentesDeDatos.FuenteDinamica;
import domain.business.Usuarios.Rol;
import domain.business.incidencias.Hecho;
import domain.business.incidencias.Multimedia;
import domain.business.Agregador.Agregador;
import java.util.ArrayList;
import java.time.LocalDate;
import domain.business.criterio.Criterio;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MetamapaTests {
  @Test
  // 1- Como persona administradora, deseo crear una colección.
  public void crearColeccion() {
    Perfil admin01 = new Perfil("Juan", "Perez", 30);
    Usuario admin = new Usuario("admin1@frba.utn.edu.ar", "algo", admin01, List.of(Rol.ADMINISTRADOR, Rol.CONTRIBUYENTE));

    if (!admin.tieneRol(Rol.ADMINISTRADOR)) {
      throw new IllegalStateException("El usuario no tiene rol de ADMINISTRADOR.");
    }

    // creamos el agregador y la fuente
    FuenteDinamica fuente = new FuenteDinamica();
    fuente.agregarHecho("Incendio",
        "",
        "incendios", 0f, 0f, LocalDate.of(2021, 6, 26), admin01, false,  new ArrayList<Multimedia>());

    ArrayList<FuenteDeDatos> lista = new ArrayList<FuenteDeDatos>();
    lista.add(fuente);

    Agregador agregador =  new Agregador(lista);

    // creamos la coleccion
    Coleccion coleccion = new Coleccion("Incendios 2025", "incendios", new ArrayList<Criterio>(), new ArrayList<Criterio>(), agregador);

    assertThat(coleccion.getHechos().size()).isEqualTo(1);
    System.out.println("Se creó una colección con el hecho: " + coleccion.getHechos().getFirst().getTitulo());
  }

  // 2- Como persona administradora, deseo poder importar hechos desde un archivo CSV
  @Test
  public void importarHechos() {
    Perfil admin01 = new Perfil("Juan", "Perez", 30);
    Usuario admin = new Usuario("admin1@frba.utn.edu.ar", "algo", admin01, List.of(Rol.ADMINISTRADOR, Rol.CONTRIBUYENTE));

    if (!admin.tieneRol(Rol.ADMINISTRADOR)) {
      throw new IllegalStateException("El usuario no tiene rol de ADMINISTRADOR.");
    }

    //path relativo
    String path = "src/main/resources/desastres_naturales_argentina.csv";
    CSVHechoParser parser = new CSVHechoParser();
    FuenteEstatica fuente = new FuenteEstatica(path, parser);

    fuente.cargarCSV(path);
    assertThat(fuente.getHechos()).isNotEmpty();
    System.out.println("La fuente " + fuente.getNombre() + " no esta vacia, tiene " + fuente.getHechos().size() + " hechos cargados de un CSV");

  }

  // 3- Como persona visualizadora, deseo navegar todos los hechos disponibles de una colección.
  @Test
  public void navegarHechosDeColeccion() {
    Perfil admin01 = new Perfil("Juan", "Perez", 30);
    Usuario admin = new Usuario("admin1@frba.utn.edu.ar", "algo", admin01, List.of(Rol.VISUALIZADOR));
    FuenteDinamica fuenteDinamica = new FuenteDinamica();
    List<Multimedia> multimedia = new LinkedList<Multimedia>();

    if (!admin.tieneRol(Rol.VISUALIZADOR)) {
      throw new IllegalStateException("El usuario no tiene rol de VISUALIZADOR.");
    }

    Hecho h1 = new Hecho("Incendio en Córdoba",
        "Se detecta foco en zona norte",
        "A",
        -31.4f,
        -64.2f,
        LocalDate.of(2025, 6, 12),
        admin01,
        false,
        multimedia
    );

    fuenteDinamica.hechos.add(h1);

    Hecho h2 = new Hecho("Incendio en BSAs",
        "Se detecta foco en zona norte",
        "B",
        -31.4f,
        -64.2f,
        LocalDate.of(2025, 6, 12),
        admin01,
        false,
        multimedia
    );
    fuenteDinamica.hechos.add(h2);
	//	fuenteDinamica.agregarHecho("Incendio en Córdoba",
	//			"Se detecta foco en zona norte",
	//			"A",
	//			-31.4f,
	//			64.2f,LocalDate.of(2025, 6, 12),admin01,false,fuenteDinamica,new ArrayList<Multimedia>());
//
	//	fuenteDinamica.agregarHecho("Incendio en BSAs",
	//			"Se detecta foco en zona norte",
	//			"B",
	//			-31.4f,
	//			64.2f,LocalDate.of(2025, 6, 12),admin01,false,fuenteDinamica,new ArrayList<Multimedia>());
    Agregador agregador = new Agregador(List.of(fuenteDinamica));
    Coleccion coleccion = new Coleccion("Incendios 2025", "incendios", new ArrayList<Criterio>(), new ArrayList<Criterio>(), agregador);
    List<Hecho> hechosMostrados = coleccion.getHechos();
    // ---------- 3. Verificaciones ----------
    assertThat(hechosMostrados)
        .containsExactlyInAnyOrder(h1, h2)   // se muestran los dos
        .allMatch(h -> !h.getEliminado());
  }

  //4 - Como persona visualizadora, deseo navegar los hechos disponibles de una colección, aplicando filtros.
  @Test
  public void navegarHechosAplicandoFiltros() {
    Perfil admin01 = new Perfil("Juan", "Perez", 30);
    Usuario admin = new Usuario("admin1@frba.utn.edu.ar", "algo", admin01, List.of(Rol.VISUALIZADOR));
    FuenteDinamica fuenteDinamica = new FuenteDinamica();
    List<Multimedia> multimedia = new LinkedList<Multimedia>();

    if (!admin.tieneRol(Rol.VISUALIZADOR)) {
      throw new IllegalStateException("El usuario no tiene rol de VISUALIZADOR.");
    }

    Hecho h1 = new Hecho("Incendio en Córdoba",
        "Se detecta foco en zona norte",
        "Incendio",
        -31.4f,
        -64.2f,
        LocalDate.of(2025, 7, 12),
        admin01,
        false,
        multimedia
    );
    fuenteDinamica.hechos.add(h1);
    Hecho h2 = new Hecho("Guerra en BSAs",
        "Se detecta foco en zona sur",
        "Guerra",
        -31.4f,
        -64.2f,
        LocalDate.of(2026, 6, 10),
        admin01,
        false,
        multimedia
    );
    fuenteDinamica.hechos.add(h2);

    Agregador agregador = new Agregador(List.of(fuenteDinamica));
    Coleccion coleccion = new Coleccion("Incendios 2025", "incendios", new ArrayList<Criterio>(), new ArrayList<Criterio>(), agregador);

    ArrayList<Criterio> filtrosPertenencia = new ArrayList<>();
    filtrosPertenencia.add(new CriterioCategoria("Incendio"));
    filtrosPertenencia.add(new CriterioFecha(LocalDate.of(2025, 6, 12),
        LocalDate.of(2025, 6, 12)));

    // ningún criterio extra de “no pertenencia”
    ArrayList<Criterio> filtrosNoPertenencia = new ArrayList<>();

    // ---------- 3. Acción: navegar con filtros ----------
    coleccion.agregarCriterioPertenencia(new CriterioCategoria("Incendio"));
    coleccion.agregarCriterioPertenencia(new CriterioFecha(LocalDate.of(2025, 6, 12),
        LocalDate.of(2026, 6, 12)));

    ArrayList<Hecho> resultado = coleccion.filtrarPorCriterios(
        coleccion.getCriterioPertenencia(), filtrosNoPertenencia);

    // ---------- 4. Verificaciones ----------
    assertThat(resultado)
        .containsExactly(h1)   // solo el hecho que cumple ambos filtros
        .doesNotContain(h2);

    System.out.println("La coleccion " + coleccion.getTitulo() + " se le aplico el filtro de Categoria: Incendio entre junio de 2025 y 2026 contiene al hecho: " + h1.getTitulo() + "y no contiene al hecho: " + h2.getTitulo());
  }


  // 5 - Como persona contribuyente, deseo poder solicitar la eliminación de un hecho.
  @Test
  public void solicitarEliminacionHecho() {
    Hecho unHecho = new Hecho("incendio", "desc", null, null, null, null, null, null, null);
    Perfil perfil1 = new Perfil("Juan", "Perez", 30);
    Usuario user1 = new Usuario("cont1@frba.utn.edu.ar", "algo", perfil1, List.of(Rol.CONTRIBUYENTE, Rol.VISUALIZADOR));

    if (!user1.tieneRol(Rol.CONTRIBUYENTE)) {
      throw new IllegalStateException("El usuario no tiene rol de CONTRIBUYENTE.");
    }
    SolicitudEliminacion solicitudEliminacion1 = new SolicitudEliminacion(unHecho, "Motivo que no contiene SPAM y no deberia rechazar la solicitud");
    assertThat(solicitudEliminacion1.getHecho()).isEqualTo(unHecho);
    assertThat(solicitudEliminacion1.getMotivo()).isEqualTo("Motivo que no contiene SPAM y no deberia rechazar la solicitud");
    System.out.println("Solicitud de eliminacion creada: " + solicitudEliminacion1);
  }

  // 6 - Como persona administradora, deseo poder aceptar o rechazar la solicitud de eliminación de un hecho.
  @Test
  public void aceptarSolicitudEliminacion() {
    Hecho unHecho = new Hecho("incendio", "desc", null, null, null, null, null, null, null);
    SolicitudEliminacion solicitudEliminacion1 = new SolicitudEliminacion(unHecho, "Motivo que no contiene SPAM y no deberia rechazar la solicitud");
    Perfil perfil1 = new Perfil("Juan", "Perez", 30);
    Usuario user1 = new Usuario("admin1@frba.utn.edu.ar", "algo", perfil1, List.of(Rol.CONTRIBUYENTE, Rol.VISUALIZADOR, Rol.ADMINISTRADOR));
    if (!user1.tieneRol(Rol.ADMINISTRADOR)) {
      throw new IllegalStateException("El usuario no tiene rol de CONTRIBUYENTE.");
    }
    solicitudEliminacion1.aceptarSolicitud();
    assertEquals(EstadoSolicitud.APROBADA, solicitudEliminacion1.getEstado());
    System.out.println("Solicitud aprobada: " + solicitudEliminacion1);
  }

  @Test
  public void rechazarSolicitudEliminacion() {
    Hecho unHecho = new Hecho("incendio", "desc", null, null, null, null, null, null, null);
    SolicitudEliminacion solicitudEliminacion1 = new SolicitudEliminacion(unHecho, "Motivo que no contiene SPAM y no deberia rechazar la solicitud");
    Perfil perfil1 = new Perfil("Juan", "Perez", 30);
    Usuario user1 = new Usuario("admin1@frba.utn.edu.ar", "algo", perfil1, List.of(Rol.CONTRIBUYENTE, Rol.VISUALIZADOR, Rol.ADMINISTRADOR));
    if (!user1.tieneRol(Rol.ADMINISTRADOR)) {
      throw new IllegalStateException("El usuario no tiene rol de CONTRIBUYENTE.");
    }
    solicitudEliminacion1.rechazarSolicitud();
    assertEquals(EstadoSolicitud.RECHAZADA, solicitudEliminacion1.getEstado());
    System.out.println("Solicitud rechazada: "+ solicitudEliminacion1);
  }

  //Como persona contribuyente, deseo poder crear un hecho a partir de una fuente dinámica.
  @Test
  public void agregarHechoAFuente() {
    Perfil perfilTest = new Perfil("Test", "Prueba", 99);
    FuenteDinamica fuenteDinamica = new FuenteDinamica();
    List<Multimedia> multimedia = new LinkedList<Multimedia>();

    Hecho nuevo = new Hecho("Incendio en Córdoba",
        "Se detecta foco en zona norte",
        "A",
        -31.4f,
        -64.2f,
        LocalDate.of(2025, 6, 12),
        perfilTest,
        false,
        multimedia
    );
    fuenteDinamica.hechos.add(nuevo);
    assertThat(fuenteDinamica.getHechos())     // se agregó a la fuente
        .containsExactly(nuevo);
    assertThat(nuevo.getFechaCarga()).isToday();
    System.out.println("Hecho agregado a fuente: " + nuevo);
  }
	/*@Test
	void contextLoads() {
	}

  //TODO: Como persona usuaria, quiero poder obtener todos los hechos de una fuente proxy demo configurada en una colección, con un nivel de antigüedad máximo de una hora.
  //@Test
  //public void testFuenteDemo(){
  //    FuenteDeDatos fuenteDemo = new FuenteProxy("", new HechoParser() {
  //        @Override
  //        public ArrayList<Hecho> parsearHecho(String path) {
  //            return ArrayList.of();
  //        }
  //    });
  // }
  // TODO: Como persona usuaria, quiero poder obtener todos los hechos de las fuentes MetaMapa configuradas en cada colección, en tiempo real.
  public void obtenerTodosLosHechos() {

  }

  // El Sistema debe permitir el rechazo de solicitudes de eliminación en forma automática cuando se detecta que se trata de spam.

  @Test
  public void rechazarSolicitudPorSpam() {
    Hecho unHecho = new Hecho("incendio", "desc", null, null, null,  null, null, null, null);
    SolicitudEliminacion solicitudEliminacion1 = new SolicitudEliminacion(unHecho, "Esta solicitud es Spam");
    assertEquals(EstadoSolicitud.RECHAZADA, solicitudEliminacion1.getEstado());
    System.out.println("Solicitud rechazada por Spam: " + solicitudEliminacion1);
  }
  // TODO: Modificación del algoritmo de consenso.
  @Test
  public void modificarAlgoritmoDeConsensoCambiaResultado() {
    // Arrange
    Perfil perfil = new Perfil("Luis", "Fernández", 40);
    FuenteDinamica fuente = new FuenteDinamica();

    fuente.agregarHecho("Contaminación Sonora", "", "urbano", 1f, 1f,
        LocalDate.of(2023, 6, 1), perfil, false, new ArrayList<>());

    List<FuenteDeDatos> fuentes = List.of(fuente);
    Agregador.getInstance().setFuentesDeDatos(fuentes);

    Coleccion coleccion = new Coleccion("Problemas Urbanos", "Eventos en la ciudad",
        new ArrayList<>(), new ArrayList<>());

    // Paso 1 – aplicar algoritmo que aprueba todos (simulado con lambda)
    coleccion.setConsenso((hecho, fuentesDeDatos) -> true);
    List<Hecho> resultadoTrue = coleccion.getHechos(ModosDeNavegacion.RESTRINGIDA);

    System.out.println("🔹 Consenso inicial: 'acepta todos'");
    resultadoTrue.forEach(h -> System.out.println("   - " + h.getTitulo()));
    assertThat(resultadoTrue).hasSize(1); // Debería devolver el hecho

    // Paso 2 – aplicar algoritmo real que requiere múltiples menciones (ej: Absoluto)
    coleccion.setConsenso(Consenso.stringToConsenso("Absoluto"));
    List<Hecho> resultadoAbsoluto = coleccion.getHechos(ModosDeNavegacion.RESTRINGIDA);

    System.out.println("🔸 Consenso cambiado a: 'Absoluto'");
    resultadoAbsoluto.forEach(h -> System.out.println("   - " + h.getTitulo()));
    assertThat(resultadoAbsoluto).isEmpty(); // Debería devolver vacío porque solo hay una fuente
  }

  // TODO: ● Navegación curada o irrestricta sobre una colección.
  @Test
  public void testNavegacionCuradaOIrrestricta() {
    // Arrange
    Perfil perfil = new Perfil("Ana", "González", 28);
    FuenteDinamica fuente = new FuenteDinamica();

    fuente.agregarHecho("Contaminación Río", "", "contaminacion", 1f, 1f,
        LocalDate.of(2023, 1, 15), perfil, false, new ArrayList<>());

    fuente.agregarHecho("Fuga Química", "", "industria", 2f, 2f,
        LocalDate.of(2023, 2, 10), perfil, false, new ArrayList<>());

    List<FuenteDeDatos> fuentes = List.of(fuente);
    Agregador.getInstance().setFuentesDeDatos(fuentes);

    Coleccion coleccion = new Coleccion("Medioambiente", "Hechos ambientales",
        new ArrayList<>(), new ArrayList<>());

    // Consenso que aprueba solo hechos cuyo título contiene "Río"
    Consenso consensoPersonalizado = (hecho, listaFuentes) -> hecho.getTitulo().contains("Río");
    coleccion.setConsenso(consensoPersonalizado);

    // Act
    List<Hecho> hechosIrrestrictos = coleccion.getHechos(ModosDeNavegacion.IRRESTRICTA);
    List<Hecho> hechosCurados = coleccion.getHechos(ModosDeNavegacion.RESTRINGIDA);

    // Log
    System.out.println("🔹 Modo IRRESTRICTA:");
    hechosIrrestrictos.forEach(h -> System.out.println("   - " + h.getTitulo()));
    System.out.println("   Total: " + hechosIrrestrictos.size());

    System.out.println("🔸 Modo RESTRINGIDA:");
    hechosCurados.forEach(h -> System.out.println("   - " + h.getTitulo()));
    System.out.println("   Total: " + hechosCurados.size());

    // Assert
    assertThat(hechosIrrestrictos).hasSize(2); // Ambos hechos deberían estar
    assertThat(hechosCurados).hasSize(1); // Solo el que tiene "Río" en el título
    assertThat(hechosCurados.get(0).getTitulo()).isEqualTo("Contaminación Río");
  }

  //TODO: Como persona administradora, quiero asociar un algoritmo de consenso a una colección.
  // Arrange
    Perfil perfil = new Perfil("Lucía", "Martínez", 35);
    Usuario admin = new Usuario("admin@frba.utn.edu.ar", "secreta", perfil,
        List.of(Rol.ADMINISTRADOR));

    assertTrue(admin.tieneRol(Rol.ADMINISTRADOR), "El usuario debe tener rol de ADMINISTRADOR");

    // Crear fuente con 2 hechos
    FuenteDinamica fuente = new FuenteDinamica();
    fuente.agregarHecho("Inundación", "", "clima", 1f, 1f,
        LocalDate.of(2023, 4, 5), perfil, false, new ArrayList<>());
    fuente.agregarHecho("Sequía", "", "clima", 2f, 2f,
        LocalDate.of(2023, 5, 10), perfil, false, new ArrayList<>());

    List<FuenteDeDatos> fuentes = List.of(fuente);
    Agregador.getInstance().setFuentesDeDatos(fuentes);

    // Crear colección sin criterios
    Coleccion coleccion = new Coleccion("Eventos Climáticos", "Fenómenos extremos",
        new ArrayList<>(), new ArrayList<>());

    // Act: el admin asocia un algoritmo de consenso
    Consenso algoritmo = Consenso.stringToConsenso("Absoluto"); // o "MayoriaSimple"
    coleccion.setConsenso(algoritmo);

    // Obtener hechos curados
    List<Hecho> hechos = coleccion.getHechos(ModosDeNavegacion.CURADA);

    // Assert
    System.out.println("🔹 Hechos consensuados usando 'Absoluto':");
    hechos.forEach(h -> System.out.println("   - " + h.getTitulo()));

    // Como solo hay una fuente, tal vez "Absoluto" devuelva 0
    assertNotNull(coleccion.getConsenso());
    assertThat(coleccion.getConsenso()).isEqualTo(algoritmo);
  }
  
  // TODO: Como visualizador o contribuyente, deseo poder seleccionar el modo de navegación de los hechos.
  @Test
  public void visualizadorOContribuyentePuedeElegirModoDeNavegacion() {
    // Arrange
    Perfil perfil = new Perfil("Emi", "Siclari", 25);
    Usuario visualizador = new Usuario("emi@frba.utn.edu.ar", "clave",
        perfil, List.of(Rol.VISUALIZADOR));

    Usuario contribuyente = new Usuario("colab@frba.utn.edu.ar", "clave",
        perfil, List.of(Rol.CONTRIBUYENTE));

    assertTrue(visualizador.tieneRol(Rol.VISUALIZADOR));
    assertTrue(contribuyente.tieneRol(Rol.CONTRIBUYENTE));

    FuenteDinamica fuente = new FuenteDinamica();
    fuente.agregarHecho("Derrame Petrolero", "", "medioambiente", 1f, 1f,
        LocalDate.of(2023, 3, 20), perfil, false, new ArrayList<>());

    fuente.agregarHecho("Incendio Forestal", "", "medioambiente", 2f, 2f,
        LocalDate.of(2023, 4, 5), perfil, false, new ArrayList<>());

    List<FuenteDeDatos> fuentes = List.of(fuente);
    Agregador.getInstance().setFuentesDeDatos(fuentes);

    Coleccion coleccion = new Coleccion("Catástrofes Naturales", "Eventos ambientales",
        new ArrayList<>(), new ArrayList<>());

    // Setear consenso que aprueba solo hechos con "Incendio"
    coleccion.setConsenso((hecho, fuentesDatos) -> hecho.getTitulo().contains("Incendio"));

    // Act
    List<Hecho> hechosModoIrrestricto = coleccion.getHechos(ModosDeNavegacion.IRRESTRICTA);
    List<Hecho> hechosModoRestringido = coleccion.getHechos(ModosDeNavegacion.CURADA);

    // Assert
    System.out.println("🔹 IRRESTRICTA (Visualizador/Contribuyente):");
    hechosModoIrrestricto.forEach(h -> System.out.println("   - " + h.getTitulo()));
    assertThat(hechosModoIrrestricto).hasSize(2); // Se ven todos

    System.out.println("🔸 CURADA (Curado por consenso):");
    hechosModoRestringido.forEach(h -> System.out.println("   - " + h.getTitulo()));
    assertThat(hechosModoRestringido).hasSize(1); // Solo el "Incendio"
    assertThat(hechosModoRestringido.get(0).getTitulo()).contains("Incendio");
  }
}
*/