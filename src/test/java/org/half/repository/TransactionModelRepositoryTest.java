package org.half.repository;
import org.half.model.TransactionModel;
import org.half.utility.ConnectionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;


public class TransactionModelRepositoryTest {
    private static long testAccountNumber = 123456789012L;
    private static long testAccountNumber2 = 123456789013L;
    private static long nonExistentAccountNumber = -123456789014L;
    private static final String testUsername = "testuser123";
    private static final String testUsername2 = "testuser124";
    private static TransactionModelRepository transactionModelRepository;

    //before any tests are ran, insert the test data into the database
    @BeforeAll
    static void initializeData() {
        //create transactionModelRepository object to use
        transactionModelRepository = new TransactionModelRepository();

        //I had to insert values into all three tables since the tables have foreign keys
        //for inserting into the user table
        String userQuery = """
                INSERT INTO User
                    (username, passwordHash, firstName, lastName, email, phoneNumber)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        //for inserting into the account table
        String queryForAccount = """
                INSERT INTO Account
                    (accountNumber, pinHash, accountType, balance, username)
                VALUES (?, ?, ?, ?, ?);
                """;
        //for inserting into the transaction table
        String queryForTransactionHistory = """
                INSERT INTO TransactionHistory
                    (transactionId, type, amount, originAccountNumber, destinationAccountNumber) VALUES (?,?,?,?,?);
                """;

        //insert the first user
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement userStatement = connection.prepareStatement(userQuery)) {

            userStatement.setString(1, testUsername);
            userStatement.setString(2, "somePasswordHash");
            userStatement.setString(3, "Gavin");
            userStatement.setString(4, "Garcia");
            userStatement.setString(5, "testuser123@example.com");
            userStatement.setString(6, "123-456-7890");

            userStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //insert the second user
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement userStatement2 = connection.prepareStatement(userQuery)) {

            userStatement2.setString(1, testUsername2);
            userStatement2.setString(2, "somePasswordHash2");
            userStatement2.setString(3, "Gavin2");
            userStatement2.setString(4, "Garcia2");
            userStatement2.setString(5, "testuser1234@example.com");
            userStatement2.setString(6, "123-456-7892");

            userStatement2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //insert the first account
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement accountStatement = connection.prepareStatement(queryForAccount)) {

            accountStatement.setLong(1, testAccountNumber);
            accountStatement.setString(2, "hashedPassword");
            accountStatement.setString(3, "CHECKING");
            accountStatement.setDouble(4, 100.00);
            accountStatement.setString(5, testUsername);

            accountStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //insert the second account
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement accountStatement2 = connection.prepareStatement(queryForAccount)) {

            accountStatement2.setLong(1, testAccountNumber2);
            accountStatement2.setString(2, "hashedPassword2");
            accountStatement2.setString(3, "CHECKING");
            accountStatement2.setDouble(4, 100.00);
            accountStatement2.setString(5, testUsername2);

            accountStatement2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //insert the first transaction
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement transactionStatement = connection.prepareStatement(queryForTransactionHistory)) {

            transactionStatement.setLong(1, -1);
            transactionStatement.setString(2, "Deposit");
            transactionStatement.setDouble(3, 100.00);
            transactionStatement.setLong(4, testAccountNumber);
            //sets the destination account to null
            transactionStatement.setNull(5, Types.INTEGER);

            transactionStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //insert the second transaction
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement transactionStatement2 = connection.prepareStatement(queryForTransactionHistory)) {

            transactionStatement2.setLong(1, -2);
            transactionStatement2.setString(2, "Deposit");
            transactionStatement2.setDouble(3, 100.00);
            transactionStatement2.setLong(4, testAccountNumber2);
            //sets the destination account to null
            transactionStatement2.setNull(5, Types.INTEGER);

            transactionStatement2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //check if printOutTransactions received an empty list
    @Test
    void printOutTransactionsWithNonExistentId(){
        assertTrue(transactionModelRepository.printOutTransactions(nonExistentAccountNumber, 1, 0).isEmpty());
    }

    //check printOutTransactions works with an existing id
    @Test
    void printOutTransactionsWithExistingId(){
        assertFalse(transactionModelRepository.printOutTransactions(testAccountNumber, 1, 0).isEmpty());
    }
    //check if addDepositOrWithdrawal returns false it a non-existent id
    @Test
    void addDepositOrWithdrawalWithNonExistentId(){
        TransactionModel transactionModel = new TransactionModel("Deposit", 100.00, nonExistentAccountNumber);
        assertFalse(transactionModelRepository.addDepositOrWithdrawal(transactionModel));
    }
    //check if addDepositOrWithdrawal works with valid information
    @Test
    void addDepositOrWithdrawalWithExistentId(){
        TransactionModel transactionModel = new TransactionModel("Deposit", 100.00, testAccountNumber);
        assertTrue(transactionModelRepository.addDepositOrWithdrawal(transactionModel));
    }
    //check if addTransfer returns false with a non-existent origin id
    @Test
    void addTransferWithNonExistentOriginId(){
        TransactionModel transactionModel = new TransactionModel("Transfer", 25.00, nonExistentAccountNumber, testAccountNumber2);
        assertFalse(transactionModelRepository.addTransfer(transactionModel));
    }
    //check if addTransfer returns false with a non-existent destination id
    @Test
    void addTransferWithNonExistentDestinationId(){
        TransactionModel transactionModel = new TransactionModel("Transfer", 25.00, testAccountNumber, nonExistentAccountNumber);
        assertFalse(transactionModelRepository.addTransfer(transactionModel));
    }
    //check if addTransfer works with valid information
    @Test
    void addTransferWithBothExistentIDs(){
        TransactionModel transactionModel = new TransactionModel("Transfer", 25.00, testAccountNumber, testAccountNumber2);
        assertTrue(transactionModelRepository.addTransfer(transactionModel));
    }

    //deletes all the test information that was placed into the database after every testcase has run
    @AfterAll
    static void cleanUp() throws SQLException {

        String userQuery = """
                DELETE FROM User
                WHERE username = ?;
                """;

        String accountQuery = """
            DELETE FROM Account
            WHERE accountNumber = ?;
            """;

        String transactionQuery = """
            DELETE FROM TransactionHistory
            WHERE originAccountNumber = ?;
            """;

        //delete the users
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement transactionStatement = connection.prepareStatement(transactionQuery)) {

            transactionStatement.setLong(1, testAccountNumber);


            transactionStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement transactionStatement2 = connection.prepareStatement(transactionQuery)) {

            transactionStatement2.setLong(1, testAccountNumber2);


            transactionStatement2.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        //delete the accounts
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement accountStatement = connection.prepareStatement(accountQuery)) {

            accountStatement.setLong(1, testAccountNumber);

            accountStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement accountStatement2 = connection.prepareStatement(accountQuery)) {

            accountStatement2.setLong(1, testAccountNumber2);

            accountStatement2.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        //delete the transactions
        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement userStatement = connection.prepareStatement(userQuery)) {

            userStatement.setString(1, testUsername);
            userStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = ConnectionFactory.getAutoCommitConnection();
             PreparedStatement userStatement2 = connection.prepareStatement(userQuery)) {

            userStatement2.setString(1, testUsername2);
            userStatement2.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
