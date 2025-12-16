package Usuarios.business.Usuarios;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "Usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
        }
)
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "usuario_id")
  private Long id;                         // <- era usuario_id en DB

  @Column(name = "email", nullable = false, length = 255)
  private String email;

  @Column(name = "contraseniaHasheada", nullable = false, length = 255)
  private String contraseniaHasheada;

  // === Roles como Enum en tabla de colección ===
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
          name = "Rol_de_Usuario",
          joinColumns = @JoinColumn(name = "rolUsuario_usuario", referencedColumnName = "usuario_id")
  )
  @Column(name = "rolUsuario_rol", nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Set<Rol> roles = new HashSet<>();

  @Column(name = "nombre", length = 255)
  private String nombre;

  @Column(name = "apellido", length = 255)
  private String apellido;
  private Integer edad;

  //@ElementCollection
  //private List<Integer> solicitudesDeEliminacion;

  //@ElementCollection
  //private List<Integer> solicitudesDeEdicion;

  public Usuario(String email, String contraseniaHasheada, String nombre, String apellido, Integer edad, Set<Rol> roles) {
    this.email = email;
    this.contraseniaHasheada = contraseniaHasheada;
    this.nombre = nombre;
    this.apellido = apellido;
    this.edad = edad;
    //this.solicitudesDeEliminacion = new ArrayList<>();
    //this.solicitudesDeEdicion = new ArrayList<>();
    this.roles = roles;
  }

  public Boolean tieneRol(Rol rol) {
    return this.getRoles().contains(rol);
  }
}