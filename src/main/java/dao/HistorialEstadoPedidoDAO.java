package dao;

import db.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.HistorialEstadoPedido;
import models.PedidoEstado;

public class HistorialEstadoPedidoDAO {

    public int registrarCambioEstado(int idPedido, PedidoEstado estado, Integer idUsuarioAccion) {
        String sql = "INSERT INTO historial_estado_pedido (idPedido, estado, idUsuarioAccion) "
                + "VALUES (?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idPedido);
            ps.setString(2, estado.name());

            if (idUsuarioAccion != null) {
                ps.setInt(3, idUsuarioAccion.intValue());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo registrar historial de estado.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se pudo obtener id generado del historial.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error registrando historial de estado", e);
        }
    }

    public List<HistorialEstadoPedido> listarPorPedido(int idPedido) {
        String sql = "SELECT idHistorial, idPedido, estado, fechaCambiado, idUsuarioAccion "
                + "FROM historial_estado_pedido "
                + "WHERE idPedido = ? "
                + "ORDER BY fechaCambiado ASC, idHistorial ASC";

        List<HistorialEstadoPedido> lista = new ArrayList<HistorialEstadoPedido>();

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistorialEstadoPedido h = new HistorialEstadoPedido();
                    h.setIdHistorial(rs.getInt("idHistorial"));
                    h.setIdPedido(rs.getInt("idPedido"));
                    h.setEstado(PedidoEstado.valueOf(rs.getString("estado")));

                    Timestamp ts = rs.getTimestamp("fechaCambiado");
                    if (ts != null) {
                        h.setFechaCambiado(ts.toLocalDateTime());
                    }

                    Object valorUsuario = rs.getObject("idUsuarioAccion");
                    if (valorUsuario != null) {
                        h.setIdUsuarioAccion(rs.getInt("idUsuarioAccion"));
                    } else {
                        h.setIdUsuarioAccion(null);
                    }

                    lista.add(h);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando historial del pedido", e);
        }

        return lista;
    }
}