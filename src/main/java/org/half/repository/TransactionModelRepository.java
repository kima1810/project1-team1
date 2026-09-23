package org.half.repository;
import org.half.model.TransactionModel;
import org.half.utility.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class TransactionModelRepository {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(TransactionModelRepository.class);

    //if items are found, it will return a list of the transactions
    /*
    public List<TransactionModel> printOutTransactions(long id){
        //create the query
        String query = "SELECT dateTime, type, amount, originAccountNumber, destinationAccountNumber " +
                "FROM TransactionHistory WHERE originAccountNumber=? OR destinationAccountNumber=? " + "ORDER BY dateTime DESC;";

        //create the list container
        List<TransactionModel> transactionList = new ArrayList<>();
        //check the connection
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)

        ) {
            //check if a value's originAccountNumber or destinationAccountNumber match the imputed it
            statement.setLong(1, id);
            statement.setLong(2, id);
            ResultSet resultSet = statement.executeQuery();
            //add it to the list
            while(resultSet.next()) {
                transactionList.add(new TransactionModel(
                    resultSet.getString("dateTime"),
                    resultSet.getString("type"),
                    resultSet.getDouble("amount"),
                    resultSet.getLong("originAccountNumber"),
                    resultSet.getLong("destinationAccountNumber")
                ));
            }

        }catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error with interacting with the database to check the transaction history");

        }
        logger.info("History successfully retrieved, no issues with the database");
        return transactionList;
    }


    //attempt at creating the method to display the transaction history data in different ways
    //if items are found, it will return a list of the transactions

    public List<TransactionModel> printOutTransactionsCustom(
            long id,
            String typeDisplayed,
            boolean orderByDateAndNotAmount,
            boolean useDESCAndNotASC,
            int limit,
            int offset) {

        String query =
                "SELECT dateTime, type, amount, originAccountNumber, destinationAccountNumber " +
                        "FROM TransactionHistory " +
                        "WHERE (originAccountNumber=? OR destinationAccountNumber=?) ";

        // Only filter by transaction type if a valid type was provided
        boolean filterByType =
                typeDisplayed.equals("Deposit") ||
                        typeDisplayed.equals("Withdrawal") ||
                        typeDisplayed.equals("Transfer");

        if (filterByType) {
            query += "AND type=? ";
        }

        // Choose what to sort by
        if (orderByDateAndNotAmount) {
            query += "ORDER BY dateTime ";
        } else {
            query += "ORDER BY amount ";
        }

        // Choose ascending or descending
        if (useDESCAndNotASC) {
            query += "DESC ";
        } else {
            query += "ASC ";
        }

        // Pagination
        query += "LIMIT ? OFFSET ?";

        List<TransactionModel> transactionList = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            int parameterIndex = 1;

            // Account number
            statement.setLong(parameterIndex++, id);
            statement.setLong(parameterIndex++, id);

            // Transaction type, if applicable
            if (filterByType) {
                statement.setString(parameterIndex++, typeDisplayed);
            }

            // Pagination
            statement.setInt(parameterIndex++, limit);
            statement.setInt(parameterIndex, offset);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                transactionList.add(new TransactionModel(
                        resultSet.getString("dateTime"),
                        resultSet.getString("type"),
                        resultSet.getDouble("amount"),
                        resultSet.getLong("originAccountNumber"),
                        resultSet.getLong("destinationAccountNumber")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error with interacting with the database to retrieve transaction history");
        }

        logger.info("History successfully retrieved, no issues with the database");

        return transactionList;
    }
    */

    public List<TransactionModel> printOutTransactions(long id, int limit, int offset){
        //create the query
        String query = "SELECT dateTime, type, amount, originAccountNumber, destinationAccountNumber " +
                "FROM TransactionHistory WHERE originAccountNumber=? OR destinationAccountNumber=? " + "ORDER BY dateTime DESC " + "LIMIT ? OFFSET ?;";

        //create the list container
        List<TransactionModel> transactionList = new ArrayList<>();
        //check the connection
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)

        ) {
            //check if a value's originAccountNumber or destinationAccountNumber match the imputed it
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setInt(3, limit);
            statement.setInt(4, offset);
            ResultSet resultSet = statement.executeQuery();
            //add it to the list
            while(resultSet.next()) {
                transactionList.add(new TransactionModel(
                        resultSet.getString("dateTime"),
                        resultSet.getString("type"),
                        resultSet.getDouble("amount"),
                        resultSet.getLong("originAccountNumber"),
                        resultSet.getLong("destinationAccountNumber")
                ));
            }

        }catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error with interacting with the database to check the transaction history");

        }
        logger.info("History successfully retrieved, no issues with the database");
        return transactionList;
    }

    //inserts a deposit or withdrawal into the table
    public boolean addDepositOrWithdrawal(TransactionModel transactionModel){
        //the query
        String query = "INSERT INTO TransactionHistory (type, amount, originAccountNumber, destinationAccountNumber) VALUES (?,?,?,?);";

        //test the connection
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            //add the attributes
            statement.setString(1, transactionModel.getType());
            statement.setDouble(2, transactionModel.getAmount());
            statement.setLong(3, transactionModel.getOriginAccountId());
            //sets the destination account to null
            statement.setNull(4, Types.INTEGER);
            //run the query and add the transaction to the table
            statement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error with interacting with the database to add a deposit or withdrawal");
            return false;
        }
        logger.info("deposit or withdrawal successfully added, no issues with the database");
        return true;
    }

    public boolean addTransfer(TransactionModel transactionModel){
        //the query
        String query = "INSERT INTO TransactionHistory (type, amount, originAccountNumber, destinationAccountNumber) VALUES (?,?,?,?);";
        //test the connection
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            //add the attributes
            statement.setString(1, transactionModel.getType());
            statement.setDouble(2, transactionModel.getAmount());
            statement.setLong(3, transactionModel.getOriginAccountId());
            //since this is a transfer, the value is set
            statement.setLong(4, transactionModel.getDestinationAccountId());
            //run the query and add the transaction to the table
            statement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error with interacting with the database to run a transfer");
            return false;
        }
        logger.info("Transfer successfully added, no issues with the database.");
        return true;
    }

}
