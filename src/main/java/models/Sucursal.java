package models;

public class Sucursal {

    private int idSucursal;
    private String nombreSucursal;
    private boolean sucursalActiva;

    public Sucursal() {
    }

    public Sucursal(int idSucursal, String nombreSucursal, boolean sucursalActiva) {
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.sucursalActiva = sucursalActiva;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public boolean isSucursalActiva() {
        return sucursalActiva;
    }

    public void setSucursalActiva(boolean sucursalActiva) {
        this.sucursalActiva = sucursalActiva;
    }

    @Override
    public String toString() {
        return "Sucursal{idSucursal=" + idSucursal + ", nombreSucursal='" + nombreSucursal + "', activa=" + sucursalActiva + "}";
    }
}
