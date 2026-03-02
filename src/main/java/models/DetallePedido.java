package models;

public class DetallePedido {

    private int idPedido;
    private int idProducto;
    private int cantidad;

    public DetallePedido() {
    }

    public DetallePedido(int idPedido, int idProducto, int cantidad) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "DetallePedido{idPedido=" + idPedido + ", idProducto=" + idProducto + ", cantidad=" + cantidad + "}";
    }
}
