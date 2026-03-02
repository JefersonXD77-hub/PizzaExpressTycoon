package models;

public class ParametroJuego {

    private int nivel;
    private int tiempoBase;

    public ParametroJuego() {
    }

    public ParametroJuego(int nivel, int tiempoBase) {
        this.nivel = nivel;
        this.tiempoBase = tiempoBase;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getTiempoBase() {
        return tiempoBase;
    }

    public void setTiempoBase(int tiempoBase) {
        this.tiempoBase = tiempoBase;
    }

    @Override
    public String toString() {
        return "ParametroJuego{nivel=" + nivel + ", tiempoBase=" + tiempoBase + "}";
    }
}
