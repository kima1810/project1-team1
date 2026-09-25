package org.half.repository;

import org.half.exceptions.UserAlreadyExists;
import org.half.model.User;
import org.half.utility.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    /* --- getUser --- */

    // Success
    @Test
    void getUser_success_userFound() throws SQLException {
        String username = "alexkim";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("firstName")).thenReturn("Alex");
            when(resultSet.getString("lastName")).thenReturn("Kim");
            when(resultSet.getString("email")).thenReturn("alex.kim@example.com");
            when(resultSet.getString("phoneNumber")).thenReturn("555-123-4567");
            when(resultSet.getString("username")).thenReturn(username);
            when(resultSet.getString("passwordHash")).thenReturn("hashedPassword123");

            User user = repository.getUser(username);

            assertNotNull(user);
            assertEquals("Alex", user.getFirstName());
            assertEquals("Kim", user.getLastName());
            assertEquals("alex.kim@example.com", user.getEmail());
            assertEquals("555-123-4567", user.getPhoneNumber());
            assertEquals(username, user.getUsername());
            assertEquals("hashedPassword123", user.getPassword());

            verify(statement).setString(1, username);
        }
    }

    // Username does not exist
    @Test
    void getUser_failure_userNotFound() throws SQLException {
        String username = "unknownuser";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            User user = repository.getUser(username);

            assertNull(user);
        }
    }

    // Database access fail
    @Test
    void getUser_failure_sqlException() throws SQLException {
        String username = "alexkim";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Connection failed"));

            User user = repository.getUser(username);

            assertNull(user);
        }
    }

    /* --- getUserByEmail --- */

    // Success
    @Test
    void getUserByEmail_success_userFound() throws SQLException {
        String email = "alex.kim@example.com";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("firstName")).thenReturn("Alex");
            when(resultSet.getString("lastName")).thenReturn("Kim");
            when(resultSet.getString("email")).thenReturn(email);
            when(resultSet.getString("phoneNumber")).thenReturn("555-123-4567");
            when(resultSet.getString("username")).thenReturn("alexkim");
            when(resultSet.getString("passwordHash")).thenReturn("hashedPassword123");

            User user = repository.getUserByEmail(email);

            assertNotNull(user);
            assertEquals(email, user.getEmail());

            verify(statement).setString(1, email);
        }
    }

    // Email does not exist
    @Test
    void getUserByEmail_failure_userNotFound() throws SQLException {
        String email = "unknown@example.com";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            User user = repository.getUserByEmail(email);

            assertNull(user);
        }
    }

    // Database access fail
    @Test
    void getUserByEmail_failure_sqlException() throws SQLException {
        String email = "alex.kim@example.com";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Connection failed"));

            User user = repository.getUserByEmail(email);

            assertNull(user);
        }
    }

    /* --- getPasswordHash --- */

    // Success
    @Test
    void getPasswordHash_success_hashFound() throws SQLException {
        String username = "alexkim";
        String passwordHash = "hashedPassword123";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("passwordHash")).thenReturn(passwordHash);

            String result = repository.getPasswordHash(username);

            assertEquals(passwordHash, result);
            verify(statement).setString(1, username);
        }
    }

    // Username does not exist
    @Test
    void getPasswordHash_failure_userNotFound() throws SQLException {
        String username = "unknownuser";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            String result = repository.getPasswordHash(username);

            assertNull(result);
        }
    }

    // Database access fail
    @Test
    void getPasswordHash_failure_sqlException() throws SQLException {
        String username = "alexkim";
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Connection failed"));

            String result = repository.getPasswordHash(username);

            assertNull(result);
        }
    }

    /* --- addUser --- */

    // Success
    @Test
    void addUser_success_newUser() throws Exception {
        User user = new User("Alex", "Kim", "alex.kim@example.com", "555-123-4567", "alexkim", "hashedPassword123");
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement selectStatement = mock(PreparedStatement.class);
        PreparedStatement insertStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(startsWith("SELECT"))).thenReturn(selectStatement);
            when(connection.prepareStatement(startsWith("INSERT"))).thenReturn(insertStatement);
            when(selectStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            repository.addUser(user);

            verify(insertStatement).setString(1, user.getFirstName());
            verify(insertStatement).setString(2, user.getLastName());
            verify(insertStatement).setString(3, user.getEmail());
            verify(insertStatement).setString(4, user.getPhoneNumber());
            verify(insertStatement).setString(5, user.getUsername());
            verify(insertStatement).setString(6, user.getPassword());
            verify(insertStatement).executeUpdate();
        }
    }

    // Username already taken
    @Test
    void addUser_failure_usernameAlreadyTaken() throws SQLException {
        User user = new User("Alex", "Kim", "alex.kim@example.com", "555-123-4567", "alexkim", "hashedPassword123");
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement selectStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(startsWith("SELECT"))).thenReturn(selectStatement);
            when(selectStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("firstName")).thenReturn("Alex");
            when(resultSet.getString("lastName")).thenReturn("Kim");
            when(resultSet.getString("email")).thenReturn("alex.kim@example.com");
            when(resultSet.getString("phoneNumber")).thenReturn("555-123-4567");
            when(resultSet.getString("username")).thenReturn(user.getUsername());
            when(resultSet.getString("passwordHash")).thenReturn("hashedPassword123");

            assertThrows(UserAlreadyExists.class, () -> repository.addUser(user));

            verify(connection, never()).prepareStatement(startsWith("INSERT"));
        }
    }

    // Insert fails with SQLException
    @Test
    void addUser_failure_sqlExceptionDuringInsert() throws SQLException {
        User user = new User("Alex", "Kim", "alex.kim@example.com", "555-123-4567", "alexkim", "hashedPassword123");
        UserRepository repository = new UserRepository();

        Connection connection = mock(Connection.class);
        PreparedStatement selectStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        try (
                MockedStatic<ConnectionFactory> connectionFactoryMock = Mockito.mockStatic(ConnectionFactory.class)
        ) {
            connectionFactoryMock.when(ConnectionFactory::getAutoCommitConnection).thenReturn(connection);
            when(connection.prepareStatement(startsWith("SELECT"))).thenReturn(selectStatement);
            when(selectStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);
            when(connection.prepareStatement(startsWith("INSERT"))).thenThrow(new SQLException("Insert failed"));

            assertDoesNotThrow(() -> repository.addUser(user));
        }
    }
}
