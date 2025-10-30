package Estadistica.business;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estadistica_cache")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Persiste todo en una sola tabla
@DiscriminatorColumn(name = "tipo_metrica", discriminatorType = DiscriminatorType.STRING) // Columna para diferenciar las subclases
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED) // Constructor para subclases
public abstract class Estadistica {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime fechaCalculo = LocalDateTime.now();

  // Puede añadir un campo para el JSON crudo del resultado, si lo deseas
  @Column(columnDefinition = "TEXT")
  private String resultadoJsonCache;
}