package dao;

import db.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.Rol;

public class RolDAO {

    public List<Rol> listarTodos() {
        String sql = "SELECT idRol, nombreRol FROM rol ORDER BY idRol ASC";
        List<Rol> lista = new ArrayList<Rol>();

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("idRol"));
                rol.setNombreRol(rs.getString("nombreRol"));
                lista.add(rol);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando roles", e);
        }

        return lista;
    }

    public Rol buscarPorNombre(String nombreRol) {
        String sql = "SELECT idRol, nombreRol FROM rol WHERE nombreRol = ?";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombreRol);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol();
                    rol.setIdRol(rs.getInt("idRol"));
                    rol.setNombreRol(rs.getString("nombreRol"));
                    return rol;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando rol por nombre", e);
        }

        return null;
    }

    public List<Rol> listarRolesGestionables() {
        String sql = "SELECT idRol, nombreRol "
                + "FROM rol "
                + "WHERE nombreRol IN ('JUGADOR', 'ADMIN_TIENDA') "
                + "ORDER BY idRol ASC";

        List<Rol> lista = new ArrayList<Rol>();

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("idRol"));
                rol.setNombreRol(rs.getString("nombreRol"));
                lista.add(rol);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando roles gestionables", e);
        }

        return lista;
    }

}
