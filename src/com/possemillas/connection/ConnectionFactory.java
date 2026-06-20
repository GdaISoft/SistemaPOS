package com.possemillas.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexiones JDBC hacia la base de datos db_semillas.
 * Único punto del proyecto donde se conocen URL/usuario/password.
 */
public class ConnectionFactory {

    private static final String URL = "jdbc:mysql://localhost:3306/db_semillas?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado en el classpath", e);
        }
    }
}
