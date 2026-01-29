package Bistak;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexioa {
    private static String driver = "com.mysql.jdbc.Driver"; // actualizado
    private static String usuario = "root";
    private static String password = "1MG32025";
    private static String url = "jdbc:mysql://localhost:3306/db_erronka2?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el driver de MySQL");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, usuario, password);
            JOptionPane.showMessageDialog(null, "MYSQL konexioa eginda");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión:\n" + e.getMessage());
            e.printStackTrace();
        }
        return con;
    }
}