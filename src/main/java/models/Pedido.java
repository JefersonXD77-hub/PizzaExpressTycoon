package models;

import java.time.LocalDateTime;

public class Pedido {

    private int idPedido;
    private int idPartida;
    private PedidoEstado estadoActual;
    private LocalDateTime fechaDePedido;
    private int tiempoLimite;                 
    private LocalDateTime tiempoDeExpiracion;
    private LocalDateTime tiempoDeFinalizacion; 
    private PedidoMotivoFinalizado motivoFinalizado; 

    public Pedido() {
    }

    public Pedido(int idPedido, int idPartida, PedidoEstado estadoActual,
            LocalDateTime fechaDePedido, int tiempoLimite,
            LocalDateTime tiempoDeExpiracion, LocalDateTime tiempoDeFinalizacion,
            PedidoMotivoFinalizado motivoFinalizado) {
        this.idPedido = idPedido;
        this.idPartida = idPartida;
        this.estadoActual = estadoActual;
        this.fechaDePedido = fechaDePedido;
        this.tiempoLimite = tiempoLimite;
        this.tiempoDeExpiracion = tiempoDeExpiracion;
        this.tiempoDeFinalizacion = tiempoDeFinalizacion;
        this.motivoFinalizado = motivoFinalizado;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public PedidoEstado getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(PedidoEstado estadoActual) {
        this.estadoActual = estadoActual;
    }

    public LocalDateTime getFechaDePedido() {
        return fechaDePedido;
    }

    public void setFechaDePedido(LocalDateTime fechaDePedido) {
        this.fechaDePedido = fechaDePedido;
    }

    public int getTiempoLimite() {
        return tiempoLimite;
    }

    public void setTiempoLimite(int tiempoLimite) {
        this.tiempoLimite = tiempoLimite;
    }

    public LocalDateTime getTiempoDeExpiracion() {
        return tiempoDeExpiracion;
    }

    public void setTiempoDeExpiracion(LocalDateTime tiempoDeExpiracion) {
        this.tiempoDeExpiracion = tiempoDeExpiracion;
    }

    public LocalDateTime getTiempoDeFinalizacion() {
        return tiempoDeFinalizacion;
    }

    public void setTiempoDeFinalizacion(LocalDateTime tiempoDeFinalizacion) {
        this.tiempoDeFinalizacion = tiempoDeFinalizacion;
    }

    public PedidoMotivoFinalizado getMotivoFinalizado() {
        return motivoFinalizado;
    }

    public void setMotivoFinalizado(PedidoMotivoFinalizado motivoFinalizado) {
        this.motivoFinalizado = motivoFinalizado;
    }

    @Override
    public String toString() {
        return "Pedido{idPedido=" + idPedido + ", idPartida=" + idPartida
                + ", estado=" + estadoActual + ", pedido=" + fechaDePedido
                + ", limite=" + tiempoLimite + ", expira=" + tiempoDeExpiracion
                + ", finaliza=" + tiempoDeFinalizacion + ", motivo=" + motivoFinalizado + "}";
    }
}
