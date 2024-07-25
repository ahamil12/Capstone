package com.cognixia.jump.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String URL = "jdbc:mysql://localhost:3306/tv_shows";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root"; // Change password if needed

    private static Connection connection = null;

    private static void makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            makeConnection();
        }
        return connection;
    }

    public static void main(String[] args) {
        try {
            Connection conn = ConnectionManager.getConnection();
            System.out.println("Connected");
            conn.close();
            System.out.println("Closed connection");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
