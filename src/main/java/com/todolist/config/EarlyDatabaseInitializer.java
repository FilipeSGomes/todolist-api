package com.todolist.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EarlyDatabaseInitializer {
    static {
        createDatabaseIfNotExists();
    }

    public static void ensureDatabase() {
        // Just reference the static initializer
    }

    private static void createDatabaseIfNotExists() {
        String masterUrl = "jdbc:sqlserver://localhost:1433;trustServerCertificate=true";
        String username = "sa";
        String password = "YourPassword123!";

        try (Connection connection = DriverManager.getConnection(masterUrl, username, password)) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM sys.databases WHERE name = 'todolist'")) {

                resultSet.next();
                if (resultSet.getInt(1) == 0) {
                    try (Statement createStatement = connection.createStatement()) {
                        createStatement.executeUpdate("CREATE DATABASE todolist");
                        System.out.println("✅ Banco de dados 'todolist' criado com sucesso!");
                    }
                } else {
                    System.out.println("✅ Banco de dados 'todolist' já existe!");
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Erro ao inicializar banco: " + e.getMessage());
        }
    }
}
