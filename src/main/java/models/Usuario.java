package models;

import java.time.LocalDateTime;

public class Usuario {

    private int idUsuario;
    private String nickname;
    private String password;
    private int idRol;
    private Integer idSucursal;      
    private boolean usuarioActivo;
    private LocalDateTime fechaCreado;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nickname, String password, int idRol, Integer idSucursal,
            boolean usuarioActivo, LocalDateTime fechaCreado) {
        this.idUsuario = idUsuario;
        this.nickname = nickname;
        this.password = password;
        this.idRol = idRol;
        this.idSucursal = idSucursal;
        this.usuarioActivo = usuarioActivo;
        this.fechaCreado = fechaCreado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public boolean isUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(boolean usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }

    public LocalDateTime getFechaCreado() {
        return fechaCreado;
    }

    public void setFechaCreado(LocalDateTime fechaCreado) {
        this.fechaCreado = fechaCreado;
    }

    @Override
    public String toString() {
        return "Usuario{idUsuario=" + idUsuario + ", nickname='" + nickname + "', idRol=" + idRol
                + ", idSucursal=" + idSucursal + ", activo=" + usuarioActivo + ", fechaCreado=" + fechaCreado + "}";
    }
}
