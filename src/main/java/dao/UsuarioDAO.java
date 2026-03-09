package dao;

import db.ConexionDB;
import dto.UsuarioSesionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import dto.UsuarioAdminDTO;
import java.util.ArrayList;
import java.util.List;
import models.Usuario;

public class UsuarioDAO {

    public UsuarioSesionDTO login(String nickname, String password) {
        String sql = "SELECT u.idUsuario, u.nickname, "
                + "       r.nombreRol, "
                + "       u.idSucursal "
                + "FROM usuario u "
                + "JOIN rol r ON r.idRol = u.idRol "
                + "WHERE u.nickname = ? AND u.password = ? AND u.usuarioActivo = 1";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nickname);
            ps.setString(2, password);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer idSucursal = (Integer) rs.getObject("idSucursal"); 
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
        String sql = "INSERT INTO usuario(nickname, password, idRol, idSucursal, usuarioActivo) "
                + "VALUES (?, ?, "
                + "       (SELECT idRol FROM rol WHERE nombreRol = 'JUGADOR'), "
                + "       ?, 1)";

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

    public List<UsuarioAdminDTO> listarTodosConRolYSucursal() {
        String sql = "SELECT u.idUsuario, u.nickname, u.idSucursal, u.usuarioActivo, "
                + "r.nombreRol, s.nombreSucursal "
                + "FROM usuario u "
                + "INNER JOIN rol r ON u.idRol = r.idRol "
                + "LEFT JOIN sucursal s ON u.idSucursal = s.idSucursal "
                + "ORDER BY u.idUsuario ASC";

        List<UsuarioAdminDTO> lista = new ArrayList<UsuarioAdminDTO>();

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UsuarioAdminDTO dto = new UsuarioAdminDTO();
                dto.setIdUsuario(rs.getInt("idUsuario"));
                dto.setNickname(rs.getString("nickname"));
                dto.setNombreRol(rs.getString("nombreRol"));
                dto.setIdSucursal((Integer) rs.getObject("idSucursal"));
                dto.setNombreSucursal(rs.getString("nombreSucursal"));
                dto.setUsuarioActivo(rs.getInt("usuarioActivo") == 1);
                lista.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando usuarios", e);
        }

        return lista;
    }

    public int crearUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuario (nickname, password, idRol, idSucursal, usuarioActivo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNickname());
            ps.setString(2, usuario.getPassword());
            ps.setInt(3, usuario.getIdRol());

            if (usuario.getIdSucursal() != null) {
                ps.setInt(4, usuario.getIdSucursal());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setBoolean(5, usuario.isUsuarioActivo());

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se insertó el usuario.");
            }

            try ( ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se obtuvo el id generado del usuario.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando usuario", e);
        }
    }

    public boolean cambiarEstadoUsuario(int idUsuario, boolean activo) {
        String sql = "UPDATE usuario SET usuarioActivo = ? WHERE idUsuario = ?";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error cambiando estado del usuario", e);
        }
    }

    public boolean actualizarUsuarioAdmin(Usuario usuario) {
        String sql = "UPDATE usuario "
                + "SET idRol = ?, idSucursal = ?, usuarioActivo = ? "
                + "WHERE idUsuario = ?";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, usuario.getIdRol());

            if (usuario.getIdSucursal() != null) {
                ps.setInt(2, usuario.getIdSucursal());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setBoolean(3, usuario.isUsuarioActivo());
            ps.setInt(4, usuario.getIdUsuario());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error actualizando usuario", e);
        }
    }

}
