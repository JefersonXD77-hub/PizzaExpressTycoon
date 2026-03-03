package dto;

public class UsuarioSesionDTO {

    private final int idUsuario;
    private final String nickname;
    private final String nombreRol;
    private final Integer idSucursal; 
    
    public UsuarioSesionDTO(int idUsuario, String nickname, String nombreRol, Integer idSucursal) {
        this.idUsuario = idUsuario;
        this.nickname = nickname;
        this.nombreRol = nombreRol;
        this.idSucursal = idSucursal;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getNickname() { return nickname; }
    public String getNombreRol() { return nombreRol; }
    public Integer getIdSucursal() { return idSucursal; }

    @Override
    public String toString() {
        return "UsuarioSesionDTO{idUsuario=" + idUsuario +
                ", nickname='" + nickname + '\'' +
                ", nombreRol='" + nombreRol + '\'' +
                ", idSucursal=" + idSucursal + '}';
    }
}