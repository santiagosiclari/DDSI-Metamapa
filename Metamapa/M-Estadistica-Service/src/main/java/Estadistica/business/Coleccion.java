package Estadistica.business;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Coleccion {
    @Getter @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Getter
    String Nombre;

    public Coleccion() {}
    public Coleccion(String Nombre) {
        this.Nombre = Nombre;
    }
}
