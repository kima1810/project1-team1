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
    public List<TransactionModel> printOutTransactions(long id){
        //create the query
        String query = "SELECT dateTime, type, amount, originAccountNumber, destinationAccountNumber " +
                "FROM TransactionHistory WHERE originAccountNumber=? OR destinationAccountNumber=?" + "ORDER BY dateTime DESC;";

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
    /*
    public List<TransactionModel> printOutTransactionsCustom(long id, String typeDisplayed, boolean dateOrAmount, boolean ASCorDESC){
        //create the query
        String query;

        if(typeDisplayed.equals("Deposit")){
            if()
        }

        else if(typeDisplayed.equals("Withdrawal")){

        }

        else if(typeDisplayed.equals("Transfer")){

        }

        else{
            query = "SELECT dateTime, type, amount, originAccountNumber, destinationAccountNumber " +
                    "FROM TransactionHistory WHERE originAccountNumber=? OR destinationAccountNumber=?" + "ORDER BY dateTime DESC;";
        }
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
*/

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
