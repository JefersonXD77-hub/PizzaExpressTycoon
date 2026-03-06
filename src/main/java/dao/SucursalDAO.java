package dao;

import db.ConexionDB;
import models.Sucursal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SucursalDAO {

    public List<Sucursal> listarActivas() {
        String sql = "SELECT idSucursal, nombreSucursal, sucursalActiva FROM sucursal WHERE sucursalActiva = 1 ORDER BY nombreSucursal";

        List<Sucursal> lista = new ArrayList<>();
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
            throw new RuntimeException("Error listando sucursales", e);
        }
        return lista;
    }
}
