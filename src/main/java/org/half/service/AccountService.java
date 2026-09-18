package org.half.service;

import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.half.security.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    // Create a new bank account and add it to database
    public static long createAccount(User user, int pin, AccountType accountType) {
        // Check if PIN is more than 4 digits long
        if (pin > 9999) {
            // Invalid PIN
            log.warn("Invalid pin. Cannot be more than 4 digits.");
            throw new IllegalArgumentException("Invalid pin. Cannot be more than 4 digits.");
        }

        // Hash the PIN
        String pinHash = PasswordService.hashPassword(String.valueOf(pin));

        // Generate a new bank account number and attempt to create the new account
        for (int i = 0; i < 10; i++) {
            // Generate a new 12-digit account number
            long accountNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);

            // Create new bank account
            Account account = new Account(user, accountNumber, pinHash, accountType, 0.00);

            try {
                // Attempt to add new bank account
                AccountRepository.addAccount(account);
            } catch (SQLException e) {
                String message = e.getMessage();
                if (message != null && message.contains("[SQLITE_CONSTRAINT_PRIMARYKEY]")) {
                    // Duplicate account number, generate a new one
                    log.warn("Account number already in use. Generating a new one...");
                } else {
                    // Something went wrong
                    log.error("Something went wrong: {}", message);
                }
                continue;
            }

            // Success, return new bank number
            return accountNumber;
        }

        // Failed to create a new account
        return -1;
    }

    // Get all the bank accounts of a given user
    public static List<Account> getAccounts(User user) {
        try {
            // Attempt to get all the accounts of the user
            return AccountRepository.getAllAccounts(user);
        } catch (SQLException e) {
            // Something went wrong
            log.error("Something went wrong: {}", e.getMessage());
        }
        return null;
    }

    public static boolean accountExists(long accountNumber) {
        return AccountRepository.accountExists(accountNumber);
    }

    public static boolean transfer(Account sourceAccount, long destinationAccountNumber, double amount) {
        if (!Double.isFinite(amount)
                || amount <= 0
                || amount > sourceAccount.getBalance()
                || sourceAccount.getAccountNumber() == destinationAccountNumber) {
            return false;
        }

        if (!AccountRepository.transferFunds(sourceAccount, destinationAccountNumber, amount)) {
            return false;
        }

        
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        //
        TransactionHistoryService.attemptAddTransfer(
                "Transfer",
                amount,
                sourceAccount.getAccountNumber(),
                destinationAccountNumber
        );
        return true;
    }

    public static void Deposit_Request(Account account, double amount) {
        //Create new Balance
        double NewBalance = account.getBalance() + amount;
        //Update Balance column in DataBase
        AccountRepository.Update_Balance(account, NewBalance);
        //Update current instance of Balance (balance stay updated throughout instance)
        account.setBalance(NewBalance);
        //Now the transaction will be added
        TransactionHistoryService.attemptAddDepositOrWithdrawal("Deposit", amount, account.getAccountNumber());

    }

    public static void Withdraw_Request(Account account, double amount) {
        //Create new Balance
        double NewBalance = account.getBalance() - amount;
        //Update Balance column in DataBase
        AccountRepository.Update_Balance(account, NewBalance);
        //Update current instance of Balance (balance stay updated throughout instance)
        account.setBalance(NewBalance);
        //Now the transaction will be added
        TransactionHistoryService.attemptAddDepositOrWithdrawal("Withdraw", amount, account.getAccountNumber());
    }
}
