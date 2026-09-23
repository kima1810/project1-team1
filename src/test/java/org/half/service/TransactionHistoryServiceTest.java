package org.half.service;
import org.junit.jupiter.api.*;


import static org.half.model.enums.AccountType.CHECKING;
import static org.junit.jupiter.api.Assertions.*;
import org.half.service.TransactionHistoryService;
import org.half.utility.ConnectionFactory;

import org.half.model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TransactionHistoryServiceTest {
    private static long testAccountNumber = 123456789012L;
    private static long testAccountNumber2 = 123456789013L;
    private static final String testUsername = "testuser123";
    private static final String testUsername2 = "testuser124";

    private TransactionHistoryService transactionHistoryService;

    //before any tests are ran, insert the test data into the database
    @BeforeAll
    static void initializeData() {
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
    //check if the attemptPrintOutTransactions returns false if the account id is non-existent
    @Test
    void attemptPrintOutTransactionsWithNonExistentId() {
        Account account = new Account(null, -1, "1234", CHECKING, 0.00);
        assertFalse(transactionHistoryService.attemptPrintOutTransactions(account));
    }
    //check if it works alright with proper input
    @Test
    void attemptPrintOutTransactionsWithExistentId() throws SQLException {
        Account account = new Account(null, testAccountNumber, "1234", CHECKING, 0.00);
        assertTrue(transactionHistoryService.attemptPrintOutTransactions(account));
    }
    //check if attemptAddDepositOrWithdrawal rejects it while using the transfer type, it is only supposed to be either Deposit or Withdrawal
    @Test
    void attemptAddDepositOrWithdrawalWithBadTransfer(){
        assertFalse(transactionHistoryService.attemptAddDepositOrWithdrawal("Transfer", 100.00, testAccountNumber));
    }
    //check if attemptAddDepositOrWithdrawal returns false if the type is just nonsense
    @Test
    void attemptAddDepositOrWithdrawalWithIncorrectType(){
        assertFalse(transactionHistoryService.attemptAddDepositOrWithdrawal("randomThing", 100.00, testAccountNumber));
    }
    //check if attemptAddDepositOrWithdrawal returns false if the amount is less than 0
    @Test
    void attemptAddDepositOrWithdrawalWithLessThanZero(){
        assertFalse(transactionHistoryService.attemptAddDepositOrWithdrawal("Deposit", -0.1, testAccountNumber));
    }
    //check if it works when the input is valid
    @Test
    void attemptAddDepositOrWithdrawalWithValidInformation(){
        assertTrue(transactionHistoryService.attemptAddDepositOrWithdrawal("Deposit", 100.00, testAccountNumber));
    }
    //check if attemptAddTransfer rejects it if the type is deposit, it is only supposed to be Transfer
    @Test
    void attemptAddTransferWithBadDepositOrWithdrawal(){
        assertFalse(transactionHistoryService.attemptAddTransfer("Deposit", 100.00, testAccountNumber, testAccountNumber2));
    }
    //check if attemptAddTransfer returns false if the type is nonsense
    @Test
    void attemptAddTransferWithIncorrectType(){
        assertFalse(transactionHistoryService.attemptAddTransfer("randomStuff", 100.00, testAccountNumber, testAccountNumber2));
    }
    // if attemptAddTransfer returns false with a negative amount
    @Test
    void attemptAddTransferWithLessThanZero(){
        assertFalse(transactionHistoryService.attemptAddTransfer("Transfer", -0.1, testAccountNumber, testAccountNumber2));
    }
    //check if attemptAddTransfer works with valid inputs
    @Test
    void attemptAddTransferWithValidInformation(){
        assertTrue(transactionHistoryService.attemptAddTransfer("Transfer", 100.00, testAccountNumber, testAccountNumber2));
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
