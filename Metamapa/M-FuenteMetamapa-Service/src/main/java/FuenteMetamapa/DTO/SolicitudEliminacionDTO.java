package java.FuenteMetamapa.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonInclude(Include.NON_NULL)
public class SolicitudEliminacionDTO {
    private String motivo;
    private String hechoAfectado;

    public SolicitudEliminacionDTO() {
    }

    public SolicitudEliminacionDTO(String motivo, String hechoAfectado) {
        this.motivo = motivo;
        this.hechoAfectado = hechoAfectado;
    }
}