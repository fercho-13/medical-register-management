package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static ConexionDB instance;
    private Connection connection;
    // Configura la IP estática de la PC de escritorio aquí, ej: "192.168.1.50"
    // Para tus pruebas locales en Docker usa "localhost" o la IP del contenedor
    private String url = "jdbc:mysql://localhost:3306/lalcec_db";
    private String username = "root";
    private String password = "password"; // Cambiar por la clave real

    private ConexionDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Error de conexión a la base de datos: " + ex.getMessage());
        }
    }

    public static Connection getConnection() {
        if (instance == null) {
            instance = new ConexionDB();
        } else {
            try {
                if (instance.connection.isClosed()) {
                    instance = new ConexionDB();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance.connection;
    }
}

