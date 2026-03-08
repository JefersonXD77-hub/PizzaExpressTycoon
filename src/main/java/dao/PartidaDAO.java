package dao;

import db.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.SQLException;
import models.Partida;

public class PartidaDAO {

    public int crearPartida(int idUsuario, int idSucursal) {
        String sql = "INSERT INTO partida (idUsuario, idSucursal, puntajeFinal, nivelMaximo, finalizado) "
                + "VALUES (?, ?, 0, 1, 0)";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idSucursal);

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo crear la partida.");
            }

            try ( ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se pudo obtener el id generado de la partida.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la partida", e);
        }
    }

    public Partida buscarPorId(int idPartida) {
        String sql = "SELECT idPartida, idUsuario, idSucursal, fechaInicio, fechaFin, "
                + "puntajeFinal, nivelMaximo, finalizado "
                + "FROM partida WHERE idPartida = ?";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPartida);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPartida(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando partida por id", e);
        }

        return null;
    }

    public boolean finalizarPartida(int idPartida, int puntajeFinal, int nivelMaximo) {
        String sql = "UPDATE partida "
                + "SET fechaFin = NOW(), puntajeFinal = ?, nivelMaximo = ?, finalizado = 1 "
                + "WHERE idPartida = ?";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, puntajeFinal);
            ps.setInt(2, nivelMaximo);
            ps.setInt(3, idPartida);

            int filas = ps.executeUpdate();
            return filas == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finalizando partida", e);
        }
    }

    public Partida obtenerPartidaActivaPorUsuario(int idUsuario) {
        String sql = "SELECT idPartida, idUsuario, idSucursal, fechaInicio, fechaFin, "
                + "puntajeFinal, nivelMaximo, finalizado "
                + "FROM partida "
                + "WHERE idUsuario = ? AND finalizado = 0 "
                + "ORDER BY idPartida DESC "
                + "LIMIT 1";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPartida(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error consultando partida activa del usuario", e);
        }

        return null;
    }

    private Partida mapearPartida(ResultSet rs) throws SQLException {
        Partida p = new Partida();
        p.setIdPartida(rs.getInt("idPartida"));
        p.setIdUsuario(rs.getInt("idUsuario"));
        p.setIdSucursal(rs.getInt("idSucursal"));

        Timestamp tsInicio = rs.getTimestamp("fechaInicio");
        if (tsInicio != null) {
            p.setFechaInicio(tsInicio.toLocalDateTime());
        }

        Timestamp tsFin = rs.getTimestamp("fechaFin");
        if (tsFin != null) {
            p.setFechaFin(tsFin.toLocalDateTime());
        }

        p.setPuntajeFinal(rs.getInt("puntajeFinal"));
        p.setNivelMaximo(rs.getInt("nivelMaximo"));
        p.setFinalizado(rs.getBoolean("finalizado"));

        return p;
    }
}
