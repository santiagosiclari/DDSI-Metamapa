package Estadistica.business.Estadistica;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(
        name = "hecho",
        schema = "dbo",
        indexes = {
                @Index(name = "hecho_categoria", columnList = "categoria"),
                @Index(name = "hecho_fechaHecho", columnList = "fecha_hecho"),
                @Index(name = "hecho_lat_long", columnList = "latitud,longitud")
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hecho {

    @Id
    // Si preferís BigInteger: cambiá a BigInteger y agregá precision=19, scale=0 en @Column
    @Column(name = "id", nullable = false)
    private BigInteger id;

    @Column(name = "titulo", length = 500, nullable = false)
    private String titulo;

    @Column(name = "descripcion", length = 4000)
    private String descripcion;

    @Column(name = "categoria", length = 200)
    private String categoria;

    @Column(name = "latitud")
    private Float latitud;

    @Column(name = "longitud")
    private Float longitud;

    @Column(name = "fecha_hecho", columnDefinition = "datetime2")
    private LocalDateTime fechaHecho;

    @Column(name = "fecha_carga", columnDefinition = "datetime2")
    private LocalDateTime fechaCarga;

    @Column(name = "fecha_modificacion", columnDefinition = "datetime2")
    private LocalDateTime fechaModificacion;

    @Column(name = "perfil_id")
    private Integer perfilId;

    @Column(name = "anonimo")
    private Boolean anonimo;

    @Column(name = "eliminado")
    private Boolean eliminado;

    // ---------- MAPAS EMBEBIDOS ----------
    // Ej.: multimedia: path -> tipo (o la estructura que necesites)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "hecho_multimedia",
            schema = "dbo",
            joinColumns = @JoinColumn(name = "hecho_id")
    )
    @MapKeyColumn(name = "clave")
    @Column(name = "valor")
    private Map<String, String> multimedia;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "hecho_metadata",
            schema = "dbo",
            joinColumns = @JoinColumn(name = "hecho_id")
    )
    @MapKeyColumn(name = "clave")
    @Column(name = "valor", length = 1000)
    private Map<String, String> metadata;

    @Column(name = "id_fuente")
    private Integer idFuente;

    // Constructor de conveniencia (opcional)
    public Hecho(
            BigInteger id,
            String titulo,
            String descripcion,
            String categoria,
            Float latitud,
            Float longitud,
            LocalDateTime fechaHecho,
            LocalDateTime fechaCarga,
            LocalDateTime fechaModificacion,
            Integer perfilId,
            Boolean anonimo,
            Boolean eliminado,
            Map<String,String> multimedia,
            Map<String,String> metadata,
            Integer idFuente
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fechaHecho = fechaHecho;
        this.fechaCarga = fechaCarga;
        this.fechaModificacion = fechaModificacion;
        this.perfilId = perfilId;
        this.anonimo = anonimo;
        this.eliminado = eliminado;
        this.multimedia = multimedia;
        this.metadata = metadata;
        this.idFuente = idFuente;
    }
}
