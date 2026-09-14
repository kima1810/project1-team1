package org.half.utility;

import org.half.exceptions.DatabaseConnectionFailure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {
    //private static final String url = System.getenv("DATABASE_URL");
    private static final String url =
            "jdbc:sqlite:C:/Users/Gavin Garcia/OneDrive/Pictures/Documents/databaseForBank/databaseForBank";


    public static Connection getAutoCommitConnection() {
        try {
            Connection connection = DriverManager.getConnection(url);
            configureForeignKeyEnforcement(connection);
            return connection;
        } catch (SQLException exception) {
            throw new DatabaseConnectionFailure("Unable to establish database connection");
        }
    }

    public static Connection getManualCommitConnection() {
        try {
            Connection connection = DriverManager.getConnection(url);
            configureForeignKeyEnforcement(connection);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException exception) {
            throw new DatabaseConnectionFailure("Unable to establish database connection");
        }
    }

    public static void configureForeignKeyEnforcement(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String config = "PRAGMA foreign_keys = ON;";
            statement.execute(config);
        }
    }

    // Example
    public static void main(String[] args) throws SQLException {
        Connection autoConnection = getAutoCommitConnection();

        Statement statement = autoConnection.createStatement();
        boolean success = statement.execute("SELECT * FROM User");
        System.out.println(success ? "Success" : "Failed");
        statement.close();

        autoConnection.close();
        Connection manualConnection = getManualCommitConnection();
        manualConnection.close();
    }
}
