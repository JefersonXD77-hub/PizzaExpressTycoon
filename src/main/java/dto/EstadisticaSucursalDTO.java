package dto;

public class EstadisticaSucursalDTO {

    private int totalPartidas;
    private int mejorPuntaje;
    private double promedioPuntaje;
    private int totalEntregados;
    private int totalCancelados;
    private int totalNoEntregados;

    public int getTotalPartidas() {
        return totalPartidas;
    }

    public void setTotalPartidas(int totalPartidas) {
        this.totalPartidas = totalPartidas;
    }

    public int getMejorPuntaje() {
        return mejorPuntaje;
    }

    public void setMejorPuntaje(int mejorPuntaje) {
        this.mejorPuntaje = mejorPuntaje;
    }

    public double getPromedioPuntaje() {
        return promedioPuntaje;
    }

    public void setPromedioPuntaje(double promedioPuntaje) {
        this.promedioPuntaje = promedioPuntaje;
    }

    public int getTotalEntregados() {
        return totalEntregados;
    }

    public void setTotalEntregados(int totalEntregados) {
        this.totalEntregados = totalEntregados;
    }

    public int getTotalCancelados() {
        return totalCancelados;
    }

    public void setTotalCancelados(int totalCancelados) {
        this.totalCancelados = totalCancelados;
    }

    public int getTotalNoEntregados() {
        return totalNoEntregados;
    }

    public void setTotalNoEntregados(int totalNoEntregados) {
        this.totalNoEntregados = totalNoEntregados;
    }
}