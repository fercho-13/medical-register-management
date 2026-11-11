package com.lalcec.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton que mantiene una única Connection (un cliente a la vez por equipo, bajo consumo).
 * Configuración: variables DB_URL/DB_USER/DB_PASSWORD, o archivo db.properties en el directorio de trabajo.
 */
public final class ConexionDB {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/lalcec_db?useSSL=false&useUnicode=true&characterEncoding=UTF-8"
            + "&serverTimezone=America/Argentina/Buenos_Aires";

    private static final ConexionDB INSTANCE = new ConexionDB();
    private final Properties props = new Properties();
    private Connection connection;

    private ConexionDB() {
        try (InputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        } catch (IOException ignored) {
            // sin archivo: se usan variables de entorno / valores por defecto
        }
    }

    public static ConexionDB getInstance() { return INSTANCE; }

    private String cfg(String env, String key, String def) {
        String v = System.getenv(env);
        return v != null && !v.isEmpty() ? v : props.getProperty(key, def);
    }

    /** Devuelve la conexión, reabriéndola si se cerró o se perdió. */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            connection = DriverManager.getConnection(
                    cfg("DB_URL", "db.url", DEFAULT_URL),
                    cfg("DB_USER", "db.user", "root"),
                    cfg("DB_PASSWORD", "db.password", ""));
        }
        return connection;
    }
}
