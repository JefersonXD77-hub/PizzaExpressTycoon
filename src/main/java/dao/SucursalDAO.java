package dao;

import db.ConexionDB;
import models.Sucursal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.Statement;

public class SucursalDAO {

    public List<Sucursal> listarActivas() {
        String sql = "SELECT idSucursal, nombreSucursal, sucursalActiva "
                + "FROM sucursal "
                + "WHERE sucursalActiva = 1 "
                + "ORDER BY nombreSucursal";

        List<Sucursal> lista = new ArrayList<Sucursal>();

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sucursal s = new Sucursal();
                s.setIdSucursal(rs.getInt("idSucursal"));
                s.setNombreSucursal(rs.getString("nombreSucursal"));
                s.setSucursalActiva(rs.getInt("sucursalActiva") == 1);
                lista.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando sucursales activas", e);
        }

        return lista;
    }

    public List<Sucursal> listarTodas() {
        String sql = "SELECT idSucursal, nombreSucursal, sucursalActiva "
                + "FROM sucursal "
                + "ORDER BY idSucursal ASC";

        List<Sucursal> lista = new ArrayList<Sucursal>();

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sucursal s = new Sucursal();
                s.setIdSucursal(rs.getInt("idSucursal"));
                s.setNombreSucursal(rs.getString("nombreSucursal"));
                s.setSucursalActiva(rs.getInt("sucursalActiva") == 1);
                lista.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando sucursales", e);
        }

        return lista;
    }

    public Sucursal buscarPorId(int idSucursal) {
        String sql = "SELECT idSucursal, nombreSucursal, sucursalActiva "
                + "FROM sucursal WHERE idSucursal = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sucursal s = new Sucursal();
                    s.setIdSucursal(rs.getInt("idSucursal"));
                    s.setNombreSucursal(rs.getString("nombreSucursal"));
                    s.setSucursalActiva(rs.getInt("sucursalActiva") == 1);
                    return s;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando sucursal por id", e);
        }

        return null;
    }

    public int insertarSucursal(Sucursal sucursal) {
        String sql = "INSERT INTO sucursal (nombreSucursal, sucursalActiva) "
                + "VALUES (?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sucursal.getNombreSucursal());
            ps.setBoolean(2, sucursal.isSucursalActiva());

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo insertar la sucursal.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("No se obtuvo el id generado de la sucursal.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error insertando sucursal", e);
        }
    }

    public boolean actualizarSucursal(Sucursal sucursal) {
        String sql = "UPDATE sucursal "
                + "SET nombreSucursal = ?, sucursalActiva = ? "
                + "WHERE idSucursal = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, sucursal.getNombreSucursal());
            ps.setBoolean(2, sucursal.isSucursalActiva());
            ps.setInt(3, sucursal.getIdSucursal());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error actualizando sucursal", e);
        }
    }

    public boolean cambiarEstadoSucursal(int idSucursal, boolean activa) {
        String sql = "UPDATE sucursal SET sucursalActiva = ? WHERE idSucursal = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setBoolean(1, activa);
            ps.setInt(2, idSucursal);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error cambiando estado de sucursal", e);
        }
    }
}