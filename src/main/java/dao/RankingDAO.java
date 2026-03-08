package dao;

import db.ConexionDB;
import dto.RankingJugadorDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RankingDAO {

    public List<RankingJugadorDTO> obtenerRankingGeneral(int limite) {
        String sql = "SELECT u.idUsuario, u.nickname, s.nombreSucursal, "
                + "MAX(p.puntajeFinal) AS mejorPuntaje, "
                + "MAX(p.nivelMaximo) AS nivelMaximo, "
                + "COUNT(p.idPartida) AS partidasJugadas "
                + "FROM partida p "
                + "INNER JOIN usuario u ON p.idUsuario = u.idUsuario "
                + "LEFT JOIN sucursal s ON u.idSucursal = s.idSucursal "
                + "WHERE p.finalizado = 1 "
                + "GROUP BY u.idUsuario, u.nickname, s.nombreSucursal "
                + "ORDER BY mejorPuntaje DESC, nivelMaximo DESC, partidasJugadas DESC "
                + "LIMIT ?";

        List<RankingJugadorDTO> lista = new ArrayList<RankingJugadorDTO>();

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limite);

            try ( ResultSet rs = ps.executeQuery()) {
                int posicion = 1;

                while (rs.next()) {
                    RankingJugadorDTO dto = new RankingJugadorDTO();
                    dto.setPosicion(posicion++);
                    dto.setIdUsuario(rs.getInt("idUsuario"));
                    dto.setNickname(rs.getString("nickname"));
                    dto.setSucursal(rs.getString("nombreSucursal"));
                    dto.setMejorPuntaje(rs.getInt("mejorPuntaje"));
                    dto.setNivelMaximo(rs.getInt("nivelMaximo"));
                    dto.setPartidasJugadas(rs.getInt("partidasJugadas"));
                    lista.add(dto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error obteniendo ranking general", e);
        }

        return lista;
    }

    public List<RankingJugadorDTO> obtenerRankingPorSucursal(int idSucursal, int limite) {
        String sql = "SELECT u.idUsuario, u.nickname, s.nombreSucursal, "
                + "MAX(p.puntajeFinal) AS mejorPuntaje, "
                + "MAX(p.nivelMaximo) AS nivelMaximo, "
                + "COUNT(p.idPartida) AS partidasJugadas "
                + "FROM partida p "
                + "INNER JOIN usuario u ON p.idUsuario = u.idUsuario "
                + "INNER JOIN sucursal s ON u.idSucursal = s.idSucursal "
                + "WHERE p.finalizado = 1 AND u.idSucursal = ? "
                + "GROUP BY u.idUsuario, u.nickname, s.nombreSucursal "
                + "ORDER BY mejorPuntaje DESC, nivelMaximo DESC, partidasJugadas DESC "
                + "LIMIT ?";

        List<RankingJugadorDTO> lista = new ArrayList<RankingJugadorDTO>();

        try ( Connection cn = ConexionDB.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);
            ps.setInt(2, limite);

            try ( ResultSet rs = ps.executeQuery()) {
                int posicion = 1;

                while (rs.next()) {
                    RankingJugadorDTO dto = new RankingJugadorDTO();
                    dto.setPosicion(posicion++);
                    dto.setIdUsuario(rs.getInt("idUsuario"));
                    dto.setNickname(rs.getString("nickname"));
                    dto.setSucursal(rs.getString("nombreSucursal"));
                    dto.setMejorPuntaje(rs.getInt("mejorPuntaje"));
                    dto.setNivelMaximo(rs.getInt("nivelMaximo"));
                    dto.setPartidasJugadas(rs.getInt("partidasJugadas"));
                    lista.add(dto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error obteniendo ranking por sucursal", e);
        }

        return lista;
    }
}
