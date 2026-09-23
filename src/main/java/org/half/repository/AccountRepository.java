package org.half.repository;

import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.utility.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class AccountRepository {
    private static final Logger log = LoggerFactory.getLogger(AccountRepository.class);

    // Add a new account to the database
    public void addAccount(Account account) throws SQLException {
        // Query to insert new account record
        String query = "INSERT INTO Account VALUES (?,?,?,?,?);";

        // Connect to database to create a new account
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set values for the new account
            statement.setLong(1, account.getAccountNumber());
            statement.setString(2, account.getPinHash());
            statement.setString(3, account.getAccountType().name());
            statement.setDouble(4, account.getBalance());
            statement.setString(5, account.getUser().getUsername());

            // Add a new account to the database
            statement.executeUpdate();
        }
    }

    // Get all the accounts of a user from the database
    public List<Account> getAllAccounts(User user) throws SQLException {
        // Query to get all accounts of the user
        String query = "SELECT * FROM Account WHERE username=?;";

        // Connect to database to query
        try(Connection connection = ConnectionFactory.getAutoCommitConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            // Pass in the username
            statement.setString(1, user.getUsername());

            // Get the result
            ResultSet resultSet = statement.executeQuery();

            // Create a list of accounts
            List<Account> accounts = new ArrayList<>();
            while(resultSet.next()) {
                // Add an account to the list
                accounts.add(new Account(
                        user,
                        resultSet.getLong("accountNumber"),
                        resultSet.getString("pinHash"),
                        AccountType.valueOf(resultSet.getString("accountType")),
                        resultSet.getDouble("balance")
                ));
            }

            // Return the list of accounts
            return accounts;
        }
    }

    public boolean accountExists(long accountNumber) {
        String query = "SELECT 1 FROM Account WHERE accountNumber=?;";

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get the balance of the given account
    public static double getBalance(Account account) {
        String query = "SELECT balance FROM Account WHERE accountNumber = ?;";

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, account.getAccountNumber());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting balance for account {}: {}", account.getAccountNumber(), e.getMessage());
        }

        return 0;
    }

    public OptionalDouble transferFunds(long sourceAccountNumber, long destinationAccountNumber, double amount) {
        String debitQuery = """
                UPDATE Account
                SET balance = balance - ?
                WHERE accountNumber = ? AND balance >= ?;
                """;
        String creditQuery = """
                UPDATE Account
                SET balance = balance + ?
                WHERE accountNumber = ?;
                """;
        String historyQuery = """
                INSERT INTO TransactionHistory
                    (type, amount, originAccountNumber, destinationAccountNumber)
                VALUES ('Transfer', ?, ?, ?);
                """;
        try (Connection connection = ConnectionFactory.getManualCommitConnection()) {
            try (PreparedStatement debitStatement = connection.prepareStatement(debitQuery)) {
                debitStatement.setDouble(1, amount);
                debitStatement.setLong(2, sourceAccountNumber);
                debitStatement.setDouble(3, amount);
                if (debitStatement.executeUpdate() != 1) {
                    log.warn("Transfer debit rejected: source account {} is missing or has insufficient funds.", sourceAccountNumber);
                    rollbackTransfer(connection);
                    return OptionalDouble.empty();
                }
            } catch (SQLException e) {
                log.error("Transfer failed while debiting account {}.", sourceAccountNumber, e);
                rollbackTransfer(connection);
                return OptionalDouble.empty();
            }

            try (PreparedStatement creditStatement = connection.prepareStatement(creditQuery)) {
                creditStatement.setDouble(1, amount);
                creditStatement.setLong(2, destinationAccountNumber);
                if (creditStatement.executeUpdate() != 1) {
                    log.warn("Transfer credit rejected: destination account {} was not updated.", destinationAccountNumber);
                    rollbackTransfer(connection);
                    return OptionalDouble.empty();
                }
            } catch (SQLException e) {
                log.error("Transfer failed while crediting account {}.", destinationAccountNumber, e);
                rollbackTransfer(connection);
                return OptionalDouble.empty();
            }

            try (PreparedStatement historyStatement = connection.prepareStatement(historyQuery)) {
                historyStatement.setDouble(1, amount);
                historyStatement.setLong(2, sourceAccountNumber);
                historyStatement.setLong(3, destinationAccountNumber);
                if (historyStatement.executeUpdate() != 1) {
                    log.warn("Transfer history was not inserted for accounts {} to {}.", sourceAccountNumber, destinationAccountNumber);
                    rollbackTransfer(connection);
                    return OptionalDouble.empty();
                }
            } catch (SQLException e) {
                log.error("Transfer failed while recording history for accounts {} to {}.", sourceAccountNumber, destinationAccountNumber, e);
                rollbackTransfer(connection);
                return OptionalDouble.empty();
            }

            OptionalDouble updatedSourceBalance;
            try {
                updatedSourceBalance = getBalance(connection, sourceAccountNumber);
                if (updatedSourceBalance.isEmpty()) {
                    log.warn("Transfer balance lookup failed: source account {} was not found.", sourceAccountNumber);
                    rollbackTransfer(connection);
                    return OptionalDouble.empty();
                }
            } catch (SQLException e) {
                log.error("Transfer failed while reading the updated balance for account {}.", sourceAccountNumber, e);
                rollbackTransfer(connection);
                return OptionalDouble.empty();
            }

            try {
                connection.commit();
            } catch (SQLException e) {
                log.error("Transfer commit failed for accounts {} to {}.", sourceAccountNumber, destinationAccountNumber, e);
                rollbackTransfer(connection);
                return OptionalDouble.empty();
            }
            return updatedSourceBalance;
        } catch (SQLException e) {
            log.error("Transfer connection failed to open or close for accounts {} to {}.", sourceAccountNumber, destinationAccountNumber, e);
            return OptionalDouble.empty();
        }
    }

    private static void rollbackTransfer(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            log.error("Transfer rollback failed; the transaction outcome needs verification.", e);
        }
    }

    private static OptionalDouble getBalance(Connection connection, long accountNumber) throws SQLException {
        String query = "SELECT balance FROM Account WHERE accountNumber = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, accountNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return OptionalDouble.empty();
                }
                return OptionalDouble.of(resultSet.getDouble("balance"));
            }
        }
    }

    public void Deposit_Balance(Account account, double amount) {
        String query = "UPDATE Account SET balance=balance+? WHERE accountNumber=?;";
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set values
            statement.setDouble(1, amount);
            statement.setLong(2, account.getAccountNumber());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Withdraw_Balance(Account account, double amount) {
        String query = "UPDATE Account SET balance=balance-? WHERE accountNumber=?;";
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set values
            statement.setDouble(1, amount);
            statement.setLong(2, account.getAccountNumber());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
