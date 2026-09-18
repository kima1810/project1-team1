package org.half.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.half.exceptions.DatabaseConnectionFailure;
import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.utility.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class AccountRepositoryTest {

    @Test
    void addAccount_shouldInsertAccountSuccessfully() throws SQLException {
        User user = mock(User.class);
        Account account = mock(Account.class);

        when(account.getAccountNumber()).thenReturn(123456L);
        when(account.getPinHash()).thenReturn("hashedPin");
        when(account.getAccountType()).thenReturn(AccountType.CHECKING);
        when(account.getBalance()).thenReturn(1000.50);
        when(account.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("john");

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(
                "INSERT INTO Account VALUES (?,?,?,?,?);"
        )).thenReturn(statement);

        try (MockedStatic<ConnectionFactory> factoryMock =
                     mockStatic(ConnectionFactory.class)) {

            factoryMock
                    .when(ConnectionFactory::getAutoCommitConnection)
                    .thenReturn(connection);

            AccountRepository.addAccount(account);

            verify(connection).prepareStatement(
                    "INSERT INTO Account VALUES (?,?,?,?,?);"
            );
            verify(statement).setLong(1, 123456L);
            verify(statement).setString(2, "hashedPin");
            verify(statement).setString(3, AccountType.CHECKING.name());
            verify(statement).setDouble(4, 1000.50);
            verify(statement).setString(5, "john");
            verify(statement).executeUpdate();
        }
    }

    @Test
    void addAccount_shouldThrowDatabaseConnectionFailure_whenConnectionFails() {
        Account account = mock(Account.class);

        try (MockedStatic<ConnectionFactory> factoryMock =
                     mockStatic(ConnectionFactory.class)) {

            factoryMock
                    .when(ConnectionFactory::getAutoCommitConnection)
                    .thenThrow(new DatabaseConnectionFailure(
                            "Unable to establish database connection"
                    ));

            DatabaseConnectionFailure thrown = assertThrows(
                    DatabaseConnectionFailure.class,
                    () -> AccountRepository.addAccount(account)
            );

            assertEquals(
                    "Unable to establish database connection",
                    thrown.getMessage()
            );
        }
    }

    @Test
    void addAccount_shouldThrowSQLException_whenExecuteUpdateFails()
            throws SQLException {

        Account account = mock(Account.class);
        User user = mock(User.class);

        when(account.getAccountNumber()).thenReturn(123456L);
        when(account.getPinHash()).thenReturn("hashedPin");
        when(account.getAccountType()).thenReturn(AccountType.CHECKING);
        when(account.getBalance()).thenReturn(1000.50);
        when(account.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("john");

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(
                "INSERT INTO Account VALUES (?,?,?,?,?);"
        )).thenReturn(statement);

        SQLException exception = new SQLException("Insert failed");

        doThrow(exception)
                .when(statement)
                .executeUpdate();

        try (MockedStatic<ConnectionFactory> factoryMock =
                     mockStatic(ConnectionFactory.class)) {

            factoryMock
                    .when(ConnectionFactory::getAutoCommitConnection)
                    .thenReturn(connection);

            SQLException thrown = assertThrows(
                    SQLException.class,
                    () -> AccountRepository.addAccount(account)
            );

            assertSame(exception, thrown);
        }
    }

    @Test
    void getAllAccounts_shouldReturnAccounts_whenRowsExist()
            throws SQLException {

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("john");

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(
                "SELECT * FROM Account WHERE username=?;"
        )).thenReturn(statement);

        when(statement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(resultSet.getLong("accountNumber"))
                .thenReturn(111111L)
                .thenReturn(222222L);

        when(resultSet.getString("pinHash"))
                .thenReturn("hash1")
                .thenReturn("hash2");

        when(resultSet.getString("accountType"))
                .thenReturn(AccountType.CHECKING.name())
                .thenReturn(AccountType.SAVINGS.name());

        when(resultSet.getDouble("balance"))
                .thenReturn(100.00)
                .thenReturn(500.00);

        try (MockedStatic<ConnectionFactory> factoryMock =
                     mockStatic(ConnectionFactory.class)) {

            factoryMock
                    .when(ConnectionFactory::getAutoCommitConnection)
                    .thenReturn(connection);

            List<Account> accounts =
                    AccountRepository.getAllAccounts(user);

            assertNotNull(accounts);
            assertEquals(2, accounts.size());

            Account first = accounts.getFirst();

            assertEquals(111111L, first.getAccountNumber());
            assertEquals("hash1", first.getPinHash());
            assertEquals(AccountType.CHECKING, first.getAccountType());
            assertEquals(100.00, first.getBalance());
            assertSame(user, first.getUser());

            Account second = accounts.get(1);

            assertEquals(222222L, second.getAccountNumber());
            assertEquals("hash2", second.getPinHash());
            assertEquals(AccountType.SAVINGS, second.getAccountType());
            assertEquals(500.00, second.getBalance());
            assertSame(user, second.getUser());

            verify(statement).setString(1, "john");
            verify(statement).executeQuery();
        }
    }

    @Test
    void getAllAccounts_shouldReturnEmptyList_whenNoRowsExist()
            throws SQLException {

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("john");

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(
                "SELECT * FROM Account WHERE username=?;"
        )).thenReturn(statement);

        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        try (MockedStatic<ConnectionFactory> factoryMock =
                     mockStatic(ConnectionFactory.class)) {

            factoryMock
                    .when(ConnectionFactory::getAutoCommitConnection)
                    .thenReturn(connection);

            List<Account> accounts =
                    AccountRepository.getAllAccounts(user);

            assertNotNull(accounts);
            assertTrue(accounts.isEmpty());

            verify(statement).setString(1, "john");
            verify(statement).executeQuery();
        }
    }

    @Test
    void getAllAccounts_shouldThrowDatabaseConnectionFailure_whenConnectionFails() {
        User user = mock(User.class);

        try (MockedStatic<ConnectionFactory> factoryMock =
                     mockStatic(ConnectionFactory.class)) {

            factoryMock
                    .when(ConnectionFactory::getAutoCommitConnection)
                    .thenThrow(new DatabaseConnectionFailure(
                            "Unable to establish database connection"
                    ));

            DatabaseConnectionFailure thrown = assertThrows(
                    DatabaseConnectionFailure.class,
                    () -> AccountRepository.getAllAccounts(user)
            );

            assertEquals(
                    "Unable to establish database connection",
                    thrown.getMessage()
            );
        }
    }

    @Test
    void getAllAccounts_shouldThrowSQLException_whenQueryFails()
            throws SQLException {

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("john");

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(
                "SELECT * FROM Account WHERE username=?;"
        )).thenReturn(statement);

        SQLException exception = new SQLException("Query failed");

        when(statement.executeQuery()).thenThrow(exception);

        try (MockedStatic<ConnectionFactory> factoryMock =
                     mockStatic(ConnectionFactory.class)) {

            factoryMock
                    .when(ConnectionFactory::getAutoCommitConnection)
                    .thenReturn(connection);

            SQLException thrown = assertThrows(
                    SQLException.class,
                    () -> AccountRepository.getAllAccounts(user)
            );

            assertSame(exception, thrown);
        }
    }
}

