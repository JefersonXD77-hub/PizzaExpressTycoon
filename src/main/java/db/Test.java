package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Test {
    public static void main(String[] args) {
        try (Connection cn = ConexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT nombreRol FROM rol")) {

            System.out.println("Conexión OK. Roles:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("nombreRol"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}