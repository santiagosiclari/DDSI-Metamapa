package Estadistica.business.Colecciones;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Entity
@Getter @Setter
public class Coleccion {
    @Id
    private UUID handle;
    private String titulo;
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Criterio> criterios = new ArrayList<>();

    public Coleccion(String titulo, String desc, ArrayList<Criterio> criterios) {
        this.titulo = titulo;
        this.descripcion = desc;
        this.criterios = criterios;
        this.handle = UUID.randomUUID();
    }

    public Coleccion() {}

    public void agregarCriterio(Criterio criterio){
        this.criterios.add(criterio);
    }
    public void eliminarCriterio(Criterio criterio){
        this.criterios.remove(criterio);
    }

}