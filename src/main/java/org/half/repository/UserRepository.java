package org.half.repository;

import org.half.model.User;
import org.half.utility.ConnectionFactory;
import org.half.exceptions.UserAlreadyExists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    public static void addUser(User user) throws UserAlreadyExists {
        if (getUser(user.getUsername()) != null) {
            throw new UserAlreadyExists("Username '" + user.getUsername() + "' is already taken.");
        }
        
        String query = "INSERT INTO User (firstName, lastName, email, phoneNumber, username, passwordHash) VALUES (?, ?, ?, ?, ?, ?)";
        try (
            Connection connection = ConnectionFactory.getAutoCommitConnection();
            PreparedStatement statement = connection.prepareStatement(query)
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

    public static String getPasswordHash(String userName){
        String query = "SELECT passwordHash FROM User WHERE username=?";
        try (
                Connection connection = ConnectionFactory.getAutoCommitConnection();
                PreparedStatement statement = connection.prepareStatement(query)

        ) {
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("passwordHash");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUser(String userName) {
        String query = "SELECT * FROM User WHERE username=?";
        try (
                Connection connection = ConnectionFactory.getAutoCommitConnection();
                PreparedStatement statement = connection.prepareStatement(query)

        ) {
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return new User(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("username"),
                        resultSet.getString("passwordHash")
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserByEmail(String email) {
        String query = "SELECT * FROM User WHERE email=?";
        try (
                Connection connection = ConnectionFactory.getAutoCommitConnection();
                PreparedStatement statement = connection.prepareStatement(query)

        ) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return new User(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("username"),
                        resultSet.getString("passwordHash")
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
