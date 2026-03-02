
package db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    private static final String URL =
            "jdbc:mysql://localhost:3306/pizza_express_tycoon"
          + "?useSSL=false&serverTimezone=America/Guatemala&allowPublicKeyRetrieval=true";

    private static final String USER = "root";       
    private static final String PASS = "12345";            

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    
}
