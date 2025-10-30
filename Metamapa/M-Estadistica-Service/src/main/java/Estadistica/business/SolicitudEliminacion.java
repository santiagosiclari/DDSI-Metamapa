package Estadistica.business;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity

public class SolicitudEliminacion {
    @Getter
    boolean spam;
    @Getter @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;

    public SolicitudEliminacion() {}
    public SolicitudEliminacion(boolean spam) {
        this.spam = spam;
    }

}
