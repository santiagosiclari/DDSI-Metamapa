package Metamapa.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Genera un archivo JS dinámico (/config.js)
 * con las URLs de servicios configuradas en application.properties.
 *
 * Esto permite que el front-end lea window.METAMAPA.API_*
 * sin hardcodear las URLs en los archivos JS.
 */
@RestController
public class ConfigController {

  @Value("${M.FuenteDinamica.Service.url}")
  private String fuenteDinamicaUrl;

  @Value("${M.Agregador.Service.url}")
  private String agregadorUrl;

  @Value("${M.Colecciones.Service.url:${M.Agregador.Service.url}}")
  private String coleccionesUrl;

  @Value("${M.Agregador.Service.url}")
  private String solicitudesUrl;

  @Value("${M.Estadistica.Service.url}")
  private String estadisticaUrl;

  @Value("${M.Usuarios.Service.url}")
  private String usuariosUrl;

  @GetMapping(value = "/config.js", produces = "application/javascript")
  public String configJs() {
    return """
            window.METAMAPA = {
                API_FUENTE_DINAMICA: '%s/api-fuentesDeDatos',
                API_AGREGADOR: '%s/api-agregador',
                API_COLECCIONES: '%s/api-colecciones',
                API_SOLICITUDES: '%s/api-solicitudes',
                API_ESTADISTICA: '%s/estadistica',
                API_USUARIOS: '%s/usuarios'
            };
            console.log("🌐 Configuración MetaMapa cargada:", window.METAMAPA);
        """.formatted(fuenteDinamicaUrl, agregadorUrl, coleccionesUrl, solicitudesUrl, estadisticaUrl, usuariosUrl);
  }
}
