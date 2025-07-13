package DTO;
import domain.business.Usuarios.Perfil;
import domain.business.tiposSolicitudes.SolicitudEdicion;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class PerfilDTO {
  private final String nombre;
  private final String apellido;
  private final Integer edad;
  private final List<SolicitudEliminacion> solicitudesDeEliminacion;
  private final List<SolicitudEdicion> solicitudesDeEdicion;
  public PerfilDTO(Perfil perfil) {
    this.nombre = perfil.getNombre();
    this.apellido = perfil.getApellido();
    this.edad = perfil.getEdad();
    this.solicitudesDeEliminacion = new ArrayList<>();
    this.solicitudesDeEdicion = new ArrayList<>();
  }
}