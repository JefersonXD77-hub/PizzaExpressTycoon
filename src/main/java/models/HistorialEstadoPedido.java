package models;

import java.time.LocalDateTime;

public class HistorialEstadoPedido {

    private int idHistorial;
    private int idPedido;
    private PedidoEstado estado;
    private LocalDateTime fechaCambiado;
    private Integer idUsuarioAccion; 

    public HistorialEstadoPedido() {
    }

    public HistorialEstadoPedido(int idHistorial, int idPedido, PedidoEstado estado,
            LocalDateTime fechaCambiado, Integer idUsuarioAccion) {
        this.idHistorial = idHistorial;
        this.idPedido = idPedido;
        this.estado = estado;
        this.fechaCambiado = fechaCambiado;
        this.idUsuarioAccion = idUsuarioAccion;
    }

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public PedidoEstado getEstado() {
        return estado;
    }

    public void setEstado(PedidoEstado estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCambiado() {
        return fechaCambiado;
    }

    public void setFechaCambiado(LocalDateTime fechaCambiado) {
        this.fechaCambiado = fechaCambiado;
    }

    public Integer getIdUsuarioAccion() {
        return idUsuarioAccion;
    }

    public void setIdUsuarioAccion(Integer idUsuarioAccion) {
        this.idUsuarioAccion = idUsuarioAccion;
    }

    @Override
    public String toString() {
        return "HistorialEstadoPedido{idHistorial=" + idHistorial + ", idPedido=" + idPedido
                + ", estado=" + estado + ", fecha=" + fechaCambiado + ", idUsuarioAccion=" + idUsuarioAccion + "}";
    }
    
}
