package sw2.lab5.Entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_request")
public class SolicitudAmistad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_request")
    private int idSolicitud;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private Usuario emisor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Usuario receptor;

    @Column(name = "status", nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime fecha;

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
