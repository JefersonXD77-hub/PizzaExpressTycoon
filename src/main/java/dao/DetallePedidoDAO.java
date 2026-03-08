package dao;

import db.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.DetallePedido;

public class DetallePedidoDAO {

    public void insertarDetalle(DetallePedido detalle) {
        String sql = "INSERT INTO detalle_pedido (idPedido, idProducto, cantidad) "
                + "VALUES (?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, detalle.getIdPedido());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error insertando detalle de pedido", e);
        }
    }

    public void insertarDetalles(List<DetallePedido> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO detalle_pedido (idPedido, idProducto, cantidad) "
                + "VALUES (?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            for (DetallePedido detalle : detalles) {
                ps.setInt(1, detalle.getIdPedido());
                ps.setInt(2, detalle.getIdProducto());
                ps.setInt(3, detalle.getCantidad());
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error insertando detalles de pedido", e);
        }
    }

    public List<DetallePedido> listarPorPedido(int idPedido) {
        String sql = "SELECT idPedido, idProducto, cantidad "
                + "FROM detalle_pedido "
                + "WHERE idPedido = ?";

        List<DetallePedido> lista = new ArrayList<DetallePedido>();

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedido d = new DetallePedido();
                    d.setIdPedido(rs.getInt("idPedido"));
                    d.setIdProducto(rs.getInt("idProducto"));
                    d.setCantidad(rs.getInt("cantidad"));
                    lista.add(d);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando detalle de pedido", e);
        }

        return lista;
    }
}