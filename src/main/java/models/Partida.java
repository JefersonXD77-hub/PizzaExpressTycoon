package models;

import java.time.LocalDateTime;

public class Partida {

    private int idPartida;
    private int idUsuario;
    private int idSucursal;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;     
    private int puntajeFinal;
    private int nivelMaximo;
    private boolean finalizado;

    public Partida() {
    }

    public Partida(int idPartida, int idUsuario, int idSucursal,
            LocalDateTime fechaInicio, LocalDateTime fechaFin,
            int puntajeFinal, int nivelMaximo, boolean finalizado) {
        this.idPartida = idPartida;
        this.idUsuario = idUsuario;
        this.idSucursal = idSucursal;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.puntajeFinal = puntajeFinal;
        this.nivelMaximo = nivelMaximo;
        this.finalizado = finalizado;
    }

    public int getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getPuntajeFinal() {
        return puntajeFinal;
    }

    public void setPuntajeFinal(int puntajeFinal) {
        this.puntajeFinal = puntajeFinal;
    }

    public int getNivelMaximo() {
        return nivelMaximo;
    }

    public void setNivelMaximo(int nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    @Override
    public String toString() {
        return "Partida{idPartida=" + idPartida + ", idUsuario=" + idUsuario + ", idSucursal=" + idSucursal
                + ", inicio=" + fechaInicio + ", fin=" + fechaFin
                + ", puntaje=" + puntajeFinal + ", nivelMaximo=" + nivelMaximo + ", finalizado=" + finalizado + "}";
    }
}
