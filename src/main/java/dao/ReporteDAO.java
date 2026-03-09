package dao;

import db.ConexionDB;
import dto.EstadisticaSucursalDTO;
import dto.RankingJugadorDTO;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import dto.EstadisticaGlobalDTO;

public class ReporteDAO {

    public EstadisticaSucursalDTO obtenerEstadisticasSucursal(int idSucursal) {
        EstadisticaSucursalDTO dto = new EstadisticaSucursalDTO();

        String sqlPartidas = "SELECT "
                + "COUNT(*) AS totalPartidas, "
                + "COALESCE(MAX(puntajeFinal), 0) AS mejorPuntaje, "
                + "COALESCE(AVG(puntajeFinal), 0) AS promedioPuntaje "
                + "FROM partida "
                + "WHERE idSucursal = ? AND finalizado = 1";

        String sqlPedidos = "SELECT "
                + "COALESCE(SUM(CASE WHEN estadoActual = 'ENTREGADA' THEN 1 ELSE 0 END), 0) AS entregados, "
                + "COALESCE(SUM(CASE WHEN estadoActual = 'CANCELADA' THEN 1 ELSE 0 END), 0) AS cancelados, "
                + "COALESCE(SUM(CASE WHEN estadoActual = 'NO_ENTREGADO' THEN 1 ELSE 0 END), 0) AS noEntregados "
                + "FROM pedido pe "
                + "INNER JOIN partida pa ON pe.idPartida = pa.idPartida "
                + "WHERE pa.idSucursal = ?";

        try ( Connection cn = ConexionDB.getConnection()) {

            try ( PreparedStatement ps = cn.prepareStatement(sqlPartidas)) {
                ps.setInt(1, idSucursal);

                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dto.setTotalPartidas(rs.getInt("totalPartidas"));
                        dto.setMejorPuntaje(rs.getInt("mejorPuntaje"));
                        dto.setPromedioPuntaje(rs.getDouble("promedioPuntaje"));
                    }
                }
            }

            try ( PreparedStatement ps = cn.prepareStatement(sqlPedidos)) {
                ps.setInt(1, idSucursal);

                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dto.setTotalEntregados(rs.getInt("entregados"));
                        dto.setTotalCancelados(rs.getInt("cancelados"));
                        dto.setTotalNoEntregados(rs.getInt("noEntregados"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error obteniendo estadísticas de sucursal", e);
        }

        return dto;
    }

    public void exportarRankingSucursalCSV(String rutaArchivo, List<RankingJugadorDTO> ranking) {
        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            bw.write("Posicion,Jugador,Sucursal,MejorPuntaje,NivelMaximo,PartidasJugadas");
            bw.newLine();

            for (RankingJugadorDTO dto : ranking) {
                bw.write(dto.getPosicion() + ","
                        + escapar(dto.getNickname()) + ","
                        + escapar(dto.getSucursal()) + ","
                        + dto.getMejorPuntaje() + ","
                        + dto.getNivelMaximo() + ","
                        + dto.getPartidasJugadas());
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error exportando ranking a CSV", e);
        }
    }

    public void exportarEstadisticasSucursalCSV(String rutaArchivo, EstadisticaSucursalDTO dto) {
        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            bw.write("Metricas,Valor");
            bw.newLine();

            bw.write("TotalPartidas," + dto.getTotalPartidas());
            bw.newLine();

            bw.write("MejorPuntaje," + dto.getMejorPuntaje());
            bw.newLine();

            bw.write("PromedioPuntaje," + dto.getPromedioPuntaje());
            bw.newLine();

            bw.write("PedidosEntregados," + dto.getTotalEntregados());
            bw.newLine();

            bw.write("PedidosCancelados," + dto.getTotalCancelados());
            bw.newLine();

            bw.write("PedidosNoEntregados," + dto.getTotalNoEntregados());
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error exportando estadísticas a CSV", e);
        }
    }

    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }

        String limpio = valor.replace("\"", "\"\"");
        return "\"" + limpio + "\"";
    }

    public EstadisticaGlobalDTO obtenerEstadisticasGlobales() {
        EstadisticaGlobalDTO dto = new EstadisticaGlobalDTO();

        String sqlPartidas = "SELECT "
                + "COUNT(*) AS totalPartidas, "
                + "COALESCE(MAX(puntajeFinal), 0) AS mejorPuntajeGlobal, "
                + "COALESCE(AVG(puntajeFinal), 0) AS promedioPuntajeGlobal "
                + "FROM partida "
                + "WHERE finalizado = 1";

        String sqlPedidos = "SELECT "
                + "COALESCE(SUM(CASE WHEN estadoActual = 'ENTREGADA' THEN 1 ELSE 0 END), 0) AS entregados, "
                + "COALESCE(SUM(CASE WHEN estadoActual = 'CANCELADA' THEN 1 ELSE 0 END), 0) AS cancelados, "
                + "COALESCE(SUM(CASE WHEN estadoActual = 'NO_ENTREGADO' THEN 1 ELSE 0 END), 0) AS noEntregados "
                + "FROM pedido";

        String sqlUsuarios = "SELECT "
                + "COALESCE(SUM(CASE WHEN r.nombreRol = 'JUGADOR' THEN 1 ELSE 0 END), 0) AS totalJugadores, "
                + "COALESCE(SUM(CASE WHEN r.nombreRol = 'ADMIN_TIENDA' THEN 1 ELSE 0 END), 0) AS totalAdminsTienda "
                + "FROM usuario u "
                + "INNER JOIN rol r ON u.idRol = r.idRol";

        String sqlSucursales = "SELECT COUNT(*) AS totalSucursales FROM sucursal";

        try ( Connection cn = ConexionDB.getConnection()) {

            try ( PreparedStatement ps = cn.prepareStatement(sqlPartidas);  ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalPartidas(rs.getInt("totalPartidas"));
                    dto.setMejorPuntajeGlobal(rs.getInt("mejorPuntajeGlobal"));
                    dto.setPromedioPuntajeGlobal(rs.getDouble("promedioPuntajeGlobal"));
                }
            }

            try ( PreparedStatement ps = cn.prepareStatement(sqlPedidos);  ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalEntregados(rs.getInt("entregados"));
                    dto.setTotalCancelados(rs.getInt("cancelados"));
                    dto.setTotalNoEntregados(rs.getInt("noEntregados"));
                }
            }

            try ( PreparedStatement ps = cn.prepareStatement(sqlUsuarios);  ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalJugadores(rs.getInt("totalJugadores"));
                    dto.setTotalAdminsTienda(rs.getInt("totalAdminsTienda"));
                }
            }

            try ( PreparedStatement ps = cn.prepareStatement(sqlSucursales);  ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalSucursales(rs.getInt("totalSucursales"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error obteniendo estadísticas globales", e);
        }

        return dto;
    }

}
