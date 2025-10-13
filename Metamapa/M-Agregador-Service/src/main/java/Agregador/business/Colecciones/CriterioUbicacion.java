package Agregador.business.Colecciones;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Objects;
import lombok.Getter;
import Agregador.business.Hechos.Hecho;

public class CriterioUbicacion extends Criterio {
  @Getter
  private Float latitud;
  @Getter
  private Float longitud;

  public CriterioUbicacion(Float latitud, Float longitud, boolean inclusion) {
    this.latitud = latitud;
    this.longitud = longitud;
    this.inclusion = inclusion;
  }

  @Override
  public boolean cumple(Hecho hechoAValidar) {
    //TODO: APROXIMAR UN RANGO
    return inclusion == Objects.equals(hechoAValidar.getLatitud(), latitud) && Objects.equals(hechoAValidar.getLongitud(), longitud);
  }

  public Predicate toPredicate(Root<Hecho> root, CriteriaBuilder cb) {
    Predicate latEqual = cb.equal(root.get("latitud"), latitud);
    Predicate lonEqual = cb.equal(root.get("longitud"), longitud);
    Predicate combined = cb.and(latEqual, lonEqual);
    return inclusion ? combined : cb.not(combined);
  }

}
