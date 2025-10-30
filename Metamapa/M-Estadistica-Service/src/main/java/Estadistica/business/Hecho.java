package Estadistica.business;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
public class Hecho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Getter
    @ManyToOne
    Provincia provincia;
    @Getter
    @ManyToOne
    Categoria categoria;
    @Getter
    @ManyToMany
    List<Coleccion> colecciones;
    @Getter
    @ManyToOne
    List<SolicitudEliminacion> solicitudesEliminacion;
    @Getter
    Hora hora;

    public Hecho() {}

    public Hecho(Provincia provincia,Categoria categoria,List<Coleccion> colecciones,List<SolicitudEliminacion> solicitudesEliminacion,Hora hora)
    {
        this.provincia = provincia;
        this.categoria = categoria;
        this.colecciones = colecciones;
        this.solicitudesEliminacion = solicitudesEliminacion;
        this.hora = hora;
    }
}
