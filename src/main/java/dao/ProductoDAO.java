package dao;

import db.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Producto;
import java.sql.Statement;
import java.math.BigDecimal;

public class ProductoDAO {

    public List<Producto> listarActivosPorSucursal(int idSucursal) {
        String sql = "SELECT idProducto, idSucursal, nombreProducto, precio, productoActivo "
                + "FROM producto "
                + "WHERE idSucursal = ? AND productoActivo = 1 "
                + "ORDER BY nombreProducto";

        List<Producto> lista = new ArrayList<Producto>();

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setIdSucursal(rs.getInt("idSucursal"));
                    p.setNombreProducto(rs.getString("nombreProducto"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setProductoActivo(rs.getBoolean("productoActivo"));
                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando productos activos por sucursal", e);
        }

        return lista;
    }

    public Producto buscarPorId(int idProducto) {
        String sql = "SELECT idProducto, idSucursal, nombreProducto, precio, productoActivo "
                + "FROM producto WHERE idProducto = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setIdSucursal(rs.getInt("idSucursal"));
                    p.setNombreProducto(rs.getString("nombreProducto"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setProductoActivo(rs.getBoolean("productoActivo"));
                    return p;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando producto por id", e);
        }

        return null;
    }
    
    public List<Producto> listarTodosPorSucursal(int idSucursal) {
    String sql = "SELECT idProducto, idSucursal, nombreProducto, precio, productoActivo "
            + "FROM producto "
            + "WHERE idSucursal = ? "
            + "ORDER BY nombreProducto";

    List<Producto> lista = new ArrayList<Producto>();

    try (Connection cn = ConexionDB.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idSucursal);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("idProducto"));
                p.setIdSucursal(rs.getInt("idSucursal"));
                p.setNombreProducto(rs.getString("nombreProducto"));
                p.setPrecio(rs.getBigDecimal("precio"));
                p.setProductoActivo(rs.getBoolean("productoActivo"));
                lista.add(p);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error listando productos por sucursal", e);
    }

    return lista;
}

public int insertarProducto(Producto producto) {
    String sql = "INSERT INTO producto (idSucursal, nombreProducto, precio, productoActivo) "
            + "VALUES (?, ?, ?, ?)";

    try (Connection cn = ConexionDB.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, producto.getIdSucursal());
        ps.setString(2, producto.getNombreProducto());
        ps.setBigDecimal(3, producto.getPrecio());
        ps.setBoolean(4, producto.isProductoActivo());

        int filas = ps.executeUpdate();
        if (filas != 1) {
            throw new SQLException("No se pudo insertar el producto.");
        }

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("No se obtuvo el id generado del producto.");

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error insertando producto", e);
    }
}

public boolean actualizarProducto(Producto producto) {
    String sql = "UPDATE producto "
            + "SET nombreProducto = ?, precio = ?, productoActivo = ? "
            + "WHERE idProducto = ? AND idSucursal = ?";

    try (Connection cn = ConexionDB.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, producto.getNombreProducto());
        ps.setBigDecimal(2, producto.getPrecio());
        ps.setBoolean(3, producto.isProductoActivo());
        ps.setInt(4, producto.getIdProducto());
        ps.setInt(5, producto.getIdSucursal());

        return ps.executeUpdate() == 1;

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error actualizando producto", e);
    }
}

public boolean cambiarEstadoProducto(int idProducto, int idSucursal, boolean activo) {
    String sql = "UPDATE producto SET productoActivo = ? "
            + "WHERE idProducto = ? AND idSucursal = ?";

    try (Connection cn = ConexionDB.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setBoolean(1, activo);
        ps.setInt(2, idProducto);
        ps.setInt(3, idSucursal);

        return ps.executeUpdate() == 1;

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error cambiando estado del producto", e);
    }
}
    
    
}