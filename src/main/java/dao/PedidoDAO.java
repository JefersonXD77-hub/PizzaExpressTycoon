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
import models.Pedido;
import models.PedidoEstado;
import models.PedidoMotivoFinalizado;

public class PedidoDAO {

    public int crearPedido(Pedido pedido) {
        String sql = "INSERT INTO pedido "
                + "(idPartida, estadoActual, tiempoLimite, tiempoDeExpiracion, motivoFinalizado) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pedido.getIdPartida());
            ps.setString(2, pedido.getEstadoActual().name());
            ps.setInt(3, pedido.getTiempoLimite());
            ps.setTimestamp(4, Timestamp.valueOf(pedido.getTiempoDeExpiracion()));

            if (pedido.getMotivoFinalizado() != null) {
                ps.setString(5, pedido.getMotivoFinalizado().name());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo insertar el pedido.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se pudo obtener el id generado del pedido.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando pedido", e);
        }
    }

    public Pedido buscarPorId(int idPedido) {
        String sql = "SELECT idPedido, idPartida, estadoActual, fechaDePedido, tiempoLimite, "
                + "tiempoDeExpiracion, tiempoDeFinalizacion, motivoFinalizado "
                + "FROM pedido WHERE idPedido = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando pedido por id", e);
        }

        return null;
    }

    public List<Pedido> listarPedidosActivosPorPartida(int idPartida) {
        String sql = "SELECT idPedido, idPartida, estadoActual, fechaDePedido, tiempoLimite, "
                + "tiempoDeExpiracion, tiempoDeFinalizacion, motivoFinalizado "
                + "FROM pedido "
                + "WHERE idPartida = ? "
                + "AND estadoActual NOT IN ('ENTREGADA', 'CANCELADA', 'NO_ENTREGADO') "
                + "ORDER BY fechaDePedido ASC";

        List<Pedido> lista = new ArrayList<Pedido>();

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPartida);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPedido(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando pedidos activos de la partida", e);
        }

        return lista;
    }

    public List<Pedido> listarTodosPorPartida(int idPartida) {
        String sql = "SELECT idPedido, idPartida, estadoActual, fechaDePedido, tiempoLimite, "
                + "tiempoDeExpiracion, tiempoDeFinalizacion, motivoFinalizado "
                + "FROM pedido "
                + "WHERE idPartida = ? "
                + "ORDER BY fechaDePedido ASC";

        List<Pedido> lista = new ArrayList<Pedido>();

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPartida);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPedido(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando pedidos de la partida", e);
        }

        return lista;
    }

    public int contarPedidosActivosPorPartida(int idPartida) {
        String sql = "SELECT COUNT(*) AS total "
                + "FROM pedido "
                + "WHERE idPartida = ? "
                + "AND estadoActual NOT IN ('ENTREGADA', 'CANCELADA', 'NO_ENTREGADO')";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPartida);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error contando pedidos activos", e);
        }

        return 0;
    }

    public boolean actualizarEstado(int idPedido, PedidoEstado nuevoEstado) {
        String sql = "UPDATE pedido SET estadoActual = ? WHERE idPedido = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idPedido);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error actualizando estado del pedido", e);
        }
    }

    public boolean finalizarPedido(int idPedido, PedidoEstado estadoFinal, PedidoMotivoFinalizado motivo) {
        String sql = "UPDATE pedido "
                + "SET estadoActual = ?, tiempoDeFinalizacion = NOW(), motivoFinalizado = ? "
                + "WHERE idPedido = ? "
                + "AND estadoActual NOT IN ('ENTREGADA', 'CANCELADA', 'NO_ENTREGADO')";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, estadoFinal.name());
            ps.setString(2, motivo.name());
            ps.setInt(3, idPedido);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finalizando pedido", e);
        }
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();

        p.setIdPedido(rs.getInt("idPedido"));
        p.setIdPartida(rs.getInt("idPartida"));
        p.setEstadoActual(PedidoEstado.valueOf(rs.getString("estadoActual")));
        p.setTiempoLimite(rs.getInt("tiempoLimite"));

        Timestamp tsPedido = rs.getTimestamp("fechaDePedido");
        if (tsPedido != null) {
            p.setFechaDePedido(tsPedido.toLocalDateTime());
        }

        Timestamp tsExp = rs.getTimestamp("tiempoDeExpiracion");
        if (tsExp != null) {
            p.setTiempoDeExpiracion(tsExp.toLocalDateTime());
        }

        Timestamp tsFin = rs.getTimestamp("tiempoDeFinalizacion");
        if (tsFin != null) {
            p.setTiempoDeFinalizacion(tsFin.toLocalDateTime());
        }

        String motivo = rs.getString("motivoFinalizado");
        if (motivo != null && !motivo.trim().isEmpty()) {
            p.setMotivoFinalizado(PedidoMotivoFinalizado.valueOf(motivo));
        }

        return p;
    }
}