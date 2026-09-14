package org.half.repository;
import org.half.model.Account;
import org.half.model.TransactionModel;
import org.half.model.enums.AccountType;
import org.half.utility.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class TransactionModelRepository {
    public static List<TransactionModel> printOutTransactions(long id){
        String query = "SELECT transactionId, dateTime, type, amount, originAccountNumber, destinationAccountNumber " +
                "FROM TransactionHistory WHERE originAccountNumber=? OR destinationAccountNumber=?" + "ORDER BY dateTime DESC;";

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)

        ) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            ResultSet resultSet = statement.executeQuery();

            List<TransactionModel> transactionList = new ArrayList<>();
            while(resultSet.next()) {
                transactionList.add(new TransactionModel(
                    resultSet.getLong("transactionId"),
                    resultSet.getString("dateTime"),
                    resultSet.getString("type"),
                    resultSet.getDouble("amount"),
                    resultSet.getLong("originAccountNumber"),
                    resultSet.getLong("destinationAccountNumber")
                ));
            }

            return transactionList;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addDepositOrWithdrawal(TransactionModel transactionModel){
        String query = "INSERT INTO TransactionHistory (transactionId, type, amount, originAccountNumber, destinationAccountNumber) VALUES (?,?,?,?,?);";

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, transactionModel.getTransactionId());
            statement.setString(2, transactionModel.getType());
            statement.setDouble(3, transactionModel.getAmount());
            statement.setLong(4, transactionModel.getOriginAccountId());
            //sets the destination account to null
            statement.setNull(5, Types.INTEGER);

            statement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean addTransfer(TransactionModel transactionModel){
        String query = "INSERT INTO TransactionHistory (transactionId, type, amount, originAccountNumber, destinationAccountNumber) VALUES (?,?,?,?,?);";

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, transactionModel.getTransactionId());
            statement.setString(2, transactionModel.getType());
            statement.setDouble(3, transactionModel.getAmount());
            statement.setLong(4, transactionModel.getOriginAccountId());
            //since this is a transfer, the value is set
            statement.setLong(5, transactionModel.getDestinationAccountId());

            statement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
