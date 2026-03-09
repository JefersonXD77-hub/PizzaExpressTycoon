package dao;

import db.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.ParametroJuego;

public class ParametroJuegoDAO {

    public ParametroJuego buscarPorNivel(int nivel) {
        String sql = "SELECT nivel, tiempoBase "
                + "FROM parametro_juego "
                + "WHERE nivel = ?";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, nivel);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ParametroJuego parametro = new ParametroJuego();
                    parametro.setNivel(rs.getInt("nivel"));
                    parametro.setTiempoBase(rs.getInt("tiempoBase"));
                    return parametro;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error buscando parámetro de juego por nivel", e);
        }

        return null;
    }

    public int obtenerTiempoBasePorNivel(int nivel) {
        ParametroJuego parametro = buscarPorNivel(nivel);
        if (parametro == null) {
            throw new RuntimeException("No existe configuración en parametro_juego para el nivel " + nivel);
        }
        return parametro.getTiempoBase();
    }

    public java.util.List<ParametroJuego> listarTodos() {
        String sql = "SELECT nivel, tiempoBase FROM parametro_juego ORDER BY nivel ASC";
        java.util.List<ParametroJuego> lista = new java.util.ArrayList<ParametroJuego>();

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ParametroJuego parametro = new ParametroJuego();
                parametro.setNivel(rs.getInt("nivel"));
                parametro.setTiempoBase(rs.getInt("tiempoBase"));
                lista.add(parametro);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error listando parámetros del juego", e);
        }

        return lista;
    }

    public boolean actualizarTiempoBase(int nivel, int tiempoBase) {
        String sql = "UPDATE parametro_juego SET tiempoBase = ? WHERE nivel = ?";

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, tiempoBase);
            ps.setInt(2, nivel);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error actualizando tiempoBase del nivel", e);
        }
    }

}
