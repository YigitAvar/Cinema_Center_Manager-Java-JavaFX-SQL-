package com.example.cinamacentermanagement.database;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseConnection {
    public static Connection connection; // static yaptık
    static Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    public static Connection getConnection() throws SQLException { // static yaptık
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
            String url = "jdbc:mysql://localhost:3306/Cinema_Center_Management?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "password";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");
        }
        return connection;
    }
}