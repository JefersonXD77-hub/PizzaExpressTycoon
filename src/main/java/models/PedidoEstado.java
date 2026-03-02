package models;

public enum PedidoEstado {
    RECIBIDA,
    PREPARANDO,
    EN_HORNO,
    ENTREGADA,
    CANCELADA,
    NO_ENTREGADO;

    public boolean esFinal() {
        return this == ENTREGADA || this == CANCELADA || this == NO_ENTREGADO;
    }
}
