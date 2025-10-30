package Estadistica.business;
import java.time.LocalTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Hora {
    @Getter @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Getter
    LocalTime hora;

    public Hora() {}
    public Hora(LocalTime hora) {
        this.hora = hora;
    }

}
