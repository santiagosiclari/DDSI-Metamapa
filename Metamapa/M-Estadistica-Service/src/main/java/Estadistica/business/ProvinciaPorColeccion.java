// Estadisticas.business.ProvinciaPorColeccion.java
package Estadistica.business;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import java.util.Map;
import lombok.*;
import java.util.UUID;

@Entity
@DiscriminatorValue("PROV_COL")
@Getter @Setter
@NoArgsConstructor
public class ProvinciaPorColeccion extends Estadistica {

  private UUID coleccionId;
  private String provinciaGanadora;
  private Long conteoMaximo;

  // Campo para guardar el mapa completo {Provincia: Conteo}
  @ElementCollection
  private Map<String, Long> conteoPorProvincia;
}