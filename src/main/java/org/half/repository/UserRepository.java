package org.half.repository;

import org.half.model.User;
import org.half.utility.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRepository {
    public static void addUser(User user) {
        String query = "INSERT INTO Users (firstName, lastName, email, phoneNumber, username, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (
            Connection connection = ConnectionFactory.getAutoCommitConnection();
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhoneNumber());
            statement.setString(5, user.getUsername());
            statement.setString(6, user.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
