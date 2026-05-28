package com.ejemplo.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String MYSQLHOST = System.getenv().getOrDefault("MYSQLHOST", "mysql.railway.internal");
    private static final String MYSQLPORT = System.getenv().getOrDefault("MYSQLPORT", "3306");
    private static final String MYSQL_DATABASE = System.getenv().getOrDefault("MYSQL_DATABASE", "railway");
    private static final String MYSQLUSER = System.getenv().getOrDefault("MYSQLUSER", "root");
    private static final String MYSQLPASSWORD = System.getenv().getOrDefault("MYSQLPASSWORD", "cAjVCaTAdHSBfqWvlmYDOPhtYWzChwkg");

    private static final String URL = "jdbc:mysql://" + MYSQLHOST + ":" + MYSQLPORT + "/" + MYSQL_DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver de MySQL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, MYSQLUSER, MYSQLPASSWORD);
    }
}
