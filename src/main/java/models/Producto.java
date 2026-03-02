package models;

import java.math.BigDecimal;

public class Producto {

    private int idProducto;
    private int idSucursal;
    private String nombreProducto;
    private boolean productoActivo;
    private BigDecimal precio;

    public Producto() {
    }

    public Producto(int idProducto, int idSucursal, String nombreProducto, boolean productoActivo, BigDecimal precio) {
        this.idProducto = idProducto;
        this.idSucursal = idSucursal;
        this.nombreProducto = nombreProducto;
        this.productoActivo = productoActivo;
        this.precio = precio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public boolean isProductoActivo() {
        return productoActivo;
    }

    public void setProductoActivo(boolean productoActivo) {
        this.productoActivo = productoActivo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Producto{idProducto=" + idProducto + ", idSucursal=" + idSucursal
                + ", nombreProducto='" + nombreProducto + "', activo=" + productoActivo + ", precio=" + precio + "}";
    }
}
