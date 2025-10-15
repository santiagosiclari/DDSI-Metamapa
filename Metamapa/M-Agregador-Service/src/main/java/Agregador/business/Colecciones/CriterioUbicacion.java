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
    //TODO: APROXIMAR UN RANGO
    return inclusion == Objects.equals(hechoAValidar.getLatitud(), latitud) && Objects.equals(hechoAValidar.getLongitud(), longitud);
  }

  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    Predicate latEqual = cb.equal(root.get("latitud"), latitud);
    Predicate lonEqual = cb.equal(root.get("longitud"), longitud);
    Predicate radEqual = cb.equal(root.get("radio"), radio);
    Predicate combined = cb.and(latEqual, lonEqual,radEqual);
    return inclusion ? combined : cb.not(combined);
  }
}