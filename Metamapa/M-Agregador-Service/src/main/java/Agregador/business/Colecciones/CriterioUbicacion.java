package Agregador.business.Colecciones;
import jakarta.persistence.Entity;
import jakarta.persistence.criteria.*;
import java.util.Objects;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;

@Entity
@Getter
public class CriterioUbicacion extends Criterio {
  private Float latitud;
  private Float longitud;
  private Integer radio;

  public CriterioUbicacion(Float latitud, Float longitud, Integer radio, boolean inclusion) {
    this.latitud = latitud;
    this.longitud = longitud;
    this.radio = radio;
    this.inclusion = inclusion;
  }

  public CriterioUbicacion() {}

  @Override
  public boolean cumple(Hecho hechoAValidar) {
    if (hechoAValidar.getLatitud() == null || hechoAValidar.getLongitud() == null)
      return false;

    double distancia = distanciaKm(latitud, longitud,
        hechoAValidar.getLatitud(), hechoAValidar.getLongitud());

    boolean dentro = distancia <= radio;
    return inclusion ? dentro : !dentro;
  }

  private double distanciaKm(Float lat1, Float lon1, Float lat2, Float lon2) {
    final Float R = 6371.00f;
    Float dLat = (float) Math.toRadians(lat2 - lat1);
    Float dLon = (float) Math.toRadians(lon2 - lon1);
    Float a = (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2));
    Float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    return R * c;
  }

  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    Predicate latEqual = cb.equal(root.get("latitud"), latitud);
    Predicate lonEqual = cb.equal(root.get("longitud"), longitud);
    Predicate radEqual = cb.equal(root.get("radio"), radio);
    Predicate combined = cb.and(latEqual, lonEqual,radEqual);
    return inclusion ? combined : cb.not(combined);
  }
}