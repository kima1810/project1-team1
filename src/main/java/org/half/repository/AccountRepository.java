package org.half.repository;

import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.utility.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    // Add a new account to the database
    public static void addAccount(Account account) throws SQLException {
        // Query
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
    public static List<Account> getAllAccounts(User user) throws SQLException {
        // Query
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

    public static boolean accountExists(long accountNumber) {
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

    public static boolean transferFunds(Account sourceAccount, long destinationAccountNumber, double amount) {
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
        //I just commented out this code here because it actually gets ran twice, once here and once in the service layer
        //since the service layer only returns true if this repository function runs smoothly, I figured it would make
        //the most sense to only have the transactionHistory logic go in the service
        /*
        String historyQuery = """
                INSERT INTO TransactionHistory
                    (type, amount, originAccountNumber, destinationAccountNumber)
                VALUES ('Transfer', ?, ?, ?);
                """;
        */
        try (Connection connection = ConnectionFactory.getManualCommitConnection()) {
            try (PreparedStatement debitStatement = connection.prepareStatement(debitQuery);
                 PreparedStatement creditStatement = connection.prepareStatement(creditQuery)
                 //PreparedStatement historyStatement = connection.prepareStatement(historyQuery)) {
            ){
                debitStatement.setDouble(1, amount);
                debitStatement.setLong(2, sourceAccount.getAccountNumber());
                debitStatement.setDouble(3, amount);
                if (debitStatement.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }

                creditStatement.setDouble(1, amount);
                creditStatement.setLong(2, destinationAccountNumber);
                if (creditStatement.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }

                /*
                historyStatement.setDouble(1, amount);
                historyStatement.setLong(2, sourceAccount.getAccountNumber());
                historyStatement.setLong(3, destinationAccountNumber);
                historyStatement.executeUpdate();
                */

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void Update_Balance(Account account, double new_amount) {
        String query = "UPDATE Account SET balance=? WHERE accountNumber=?;";
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set values
            statement.setDouble(1, new_amount);
            statement.setLong(2, account.getAccountNumber());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
