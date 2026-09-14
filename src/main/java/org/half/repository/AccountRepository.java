package org.half.repository;

import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.utility.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    public static boolean addAccount(Account account) {
        String query = "INSERT INTO Account VALUES (?,?,?,?,?);";

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set values
            statement.setLong(1, account.getAccountNumber());
            statement.setString(2, account.getPinHash());
            statement.setString(3, account.getAccountType().name());
            statement.setDouble(4, account.getBalance());
            statement.setString(5, account.getUser().getUsername());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<Account> getAllAccounts(User user) {
        String query = "SELECT * FROM Account WHERE username=?;";

        try(Connection connection = ConnectionFactory.getAutoCommitConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getUsername());
            ResultSet resultSet = statement.executeQuery();

            List<Account> accounts = new ArrayList<>();
            while(resultSet.next()) {
                accounts.add(new Account(
                        user,
                        resultSet.getLong("accountNumber"),
                        resultSet.getString("pinHash"),
                        AccountType.valueOf(resultSet.getString("accountType")),
                        resultSet.getDouble("balance")
                ));
            }

            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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
