package dao;

import db.ConexionDB;
import dto.UsuarioSesionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioDAO {

    public UsuarioSesionDTO login(String nickname, String password) {
        String sql = "SELECT u.idUsuario, u.nickname, " +
            "       r.nombreRol, " +
            "       u.idSucursal " +
            "FROM usuario u " +
            "JOIN rol r ON r.idRol = u.idRol " +
            "WHERE u.nickname = ? AND u.password = ? AND u.usuarioActivo = 1";

 

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

    public boolean existeNickname(String nickname) {
        String sql = "SELECT 1 FROM usuario WHERE nickname = ? LIMIT 1";
        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();

            throw new RuntimeException("Error consultando nickname", e);
        }
    }

    public int crearJugador(String nickname, String password, int idSucursal) {
        String sql = "INSERT INTO usuario(nickname, password, idRol, idSucursal, usuarioActivo) " +
            "VALUES (?, ?, " +
            "       (SELECT idRol FROM rol WHERE nombreRol = 'JUGADOR'), " +
            "       ?, 1)";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nickname);
            ps.setString(2, password);
            ps.setInt(3, idSucursal);

            int affected = ps.executeUpdate();
            if (affected != 1) {
                throw new SQLException("No se insertó el usuario.");
            }

            try ( ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("No se pudo obtener id generado.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando jugador", e);
        }
    }
}
