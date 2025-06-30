package domain.Persistencia;

import domain.business.tiposSolicitudes.EstadoSolicitud;
import domain.business.tiposSolicitudes.SolicitudEliminacion;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.Getter;

public class RepositorioSolEliminacion {
  @Getter
  private ArrayList<SolicitudEliminacion> solicitudes;

  public RepositorioSolEliminacion() {
    solicitudes = new ArrayList<SolicitudEliminacion>();
  }

  public void agregarSolicitud(SolicitudEliminacion solicitud) {
    solicitudes.add(solicitud);
  }

  public void eliminarSolicitud(SolicitudEliminacion solicitud) {
    solicitudes.remove(solicitud);
  }
  //podira ser
//  public SolicitudEliminacion buscarSolicitud(int id) {
//
//  }

  public ArrayList<SolicitudEliminacion> getSolicitudesPendientes() {
    return solicitudes.stream().filter(soli -> soli.getEstado() == EstadoSolicitud.PENDIENTE).collect(Collectors.toCollection(ArrayList::new));
  }

  public ArrayList<SolicitudEliminacion> getSolicitudesAprobadas(){
    return solicitudes.stream().filter(soli -> soli.getEstado() == EstadoSolicitud.APROBADA).collect(Collectors.toCollection(ArrayList::new));
  }


}
