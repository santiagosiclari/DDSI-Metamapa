package Estadistica.business;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Categoria {
    @Getter @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Getter
    String Descripcion;

    public Categoria(){}

    public Categoria(String descripcion) {
        this.Descripcion = descripcion;
    }
}
