package com.cognixia.jump;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu();

        try {
            menu.establishConnections();
            menu.run(); // Starts the initial menu

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection to database: Failed");
            e.printStackTrace();
        } finally {
            try {
                menu.close();
            } catch (SQLException e) {
                System.out.println("Closure to database: Failed");
                e.printStackTrace();
            }
        }
    }
}
