package dao;

import db.ConexionDB;
import dto.UsuarioSesionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    public UsuarioSesionDTO login(String nickname, String password) {
        String sql = """
            SELECT u.idUsuario, u.nickname,
                   r.nombreRol,
                   u.idSucursal
            FROM usuario u
            JOIN rol r ON r.idRol = u.idRol
            WHERE u.nickname = ? AND u.password = ? AND u.usuarioActivo = 1
        """;

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nickname);
            ps.setString(2, password);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer idSucursal = (Integer) rs.getObject("idSucursal"); // soporta NULL
                    return new UsuarioSesionDTO(
                            rs.getInt("idUsuario"),
                            rs.getString("nickname"),
                            rs.getString("nombreRol"),
                            idSucursal
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
