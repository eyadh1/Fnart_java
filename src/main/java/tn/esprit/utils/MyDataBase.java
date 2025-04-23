package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static MyDataBase instance;
    private final String URL = "jdbc:mysql://127.0.0.1:3306/fnart";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private Connection cnx;

    private MyDataBase() {
        try {
            // Load the JDBC driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Try to establish the connection
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure you have the MySQL connector dependency in your pom.xml");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database. Please check:");
            System.err.println("1. Is MySQL server running?");
            System.err.println("2. Does database 'fnart' exist?");
            System.err.println("3. Are username and password correct?");
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getCnx() {
        if (cnx == null) {
            throw new RuntimeException("Database connection failed to initialize. Check the error messages above.");
        }
        return cnx;
    }
}