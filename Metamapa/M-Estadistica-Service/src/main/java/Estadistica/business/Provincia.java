package Estadistica.business;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Provincia {
    @Getter @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Getter
    String Nombre;

    public Provincia() {}
    public Provincia(String Nombre) {
        this.Nombre = Nombre;
    }
}
