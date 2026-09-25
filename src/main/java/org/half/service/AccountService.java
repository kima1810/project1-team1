package org.half.service;

import org.half.exceptions.IllegalPinLength;
import org.half.exceptions.InsufficientFundsException;
import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.half.security.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.ThreadLocalRandom;

public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final TransactionHistoryService transactionHistoryService;


    public AccountService(AccountRepository accountRepository, TransactionHistoryService transactionHistoryService) {
        this.accountRepository =  accountRepository;
        this.transactionHistoryService = transactionHistoryService;
    }

    // Create a new bank account and add it to database
    public long createAccount(User user, int pin, AccountType accountType) throws IllegalPinLength {
        // Check if PIN is more than 4 digits long
        if (pin > 9999) {
            // Invalid PIN
            log.warn("Invalid pin. Cannot be more than 4 digits.");
            throw new IllegalPinLength("Invalid pin. Cannot be more than 4 digits.");
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
                accountRepository.addAccount(account);
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

    // Method to verify if the entered user PIN matches the account PIN hash
    public boolean verifyAccount(Account account, int userInputPIN) throws IllegalPinLength {
        // Check if user input is valid
        if (userInputPIN > 9999) {
            // Input PIN cannot be more than 4 digits
            log.warn("Invalid pin. Cannot be more than 4 digits.");
            throw new IllegalPinLength("Invalid pin. Cannot be more than 4 digits.");
        }

        // Return ture if password is correct, else false
        return PasswordService.verifyPassword(String.valueOf(userInputPIN), account.getPinHash());
    }

    // Get all the bank accounts of a given user
    public List<Account> getAccounts(User user) {
        try {
            // Attempt to get all the accounts of the user
            return accountRepository.getAllAccounts(user);
        } catch (SQLException e) {
            // Something went wrong
            log.error("Something went wrong: {}", e.getMessage());
        }
        return null;
    }

    public boolean accountExists(long accountNumber) {
        return accountRepository.accountExists(accountNumber);
    }

    public boolean transfer(Account sourceAccount, long destinationAccountNumber, double amount) {
        if (!Double.isFinite(amount)
                || amount <= 0
                || !hasAtMostTwoDecimalPlaces(amount)
                || sourceAccount.getAccountNumber() == destinationAccountNumber) {
            return false;
        }

        OptionalDouble updatedSourceBalance = accountRepository.transferFunds(
                sourceAccount.getAccountNumber(),
                destinationAccountNumber,
                amount
        );
        if (updatedSourceBalance.isEmpty()) {
            return false;
        }

        sourceAccount.getBalance();
        return true;
    }

    public void Deposit_Request(Account account, double amount) {
        validateMoneyAmount(amount);

        //Negative value check
        if (amount < 0) {
            log.warn("Failed deposit attempt: Account {} entered a negative amount (${}).", String.format("%s ****%04d", account.getAccountType(), (account.getAccountNumber() % 10000)), amount);
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        if (amount > 1_000_000) {
            log.warn("Failed deposit attempt: Account {} entered amount exceeding $1,000,000 (${}).", String.format("%s ****%04d", account.getAccountType(), (account.getAccountNumber() % 10000)), amount);
            throw new IllegalArgumentException("Amount cannot exceeds $1,000,000.");
        }

        try {
            //Update Balance column in DataBase
            accountRepository.Deposit_Balance(account, amount);
            //Now the transaction will be added
            transactionHistoryService.attemptAddDepositOrWithdrawal("Deposit", amount, account.getAccountNumber());
        } catch (Exception e) {
            log.error("System error during deposit for Account {}: {}", String.format("%s ****%04d", account.getAccountType(), (account.getAccountNumber() % 10000)), e.getMessage(), e);
            throw e;
        }
    }

    public void Withdraw_Request(Account account, double amount) {
        validateMoneyAmount(amount);

        //Negative value check
        if (amount < 0) {
            log.warn("Failed Withdraw attempt: Account {} entered a negative amount (${}).", String.format("%s ****%04d", account.getAccountType(), (account.getAccountNumber() % 10000)), amount);
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        //Overdraft
        if (account.getBalance() < amount) {
            log.warn("Failed withdrawal attempt: Account {} attempted overdraft. Balance: ${}, Attempted: ${}", String.format("%s ****%04d", account.getAccountType(), (account.getAccountNumber() % 10000)), account.getBalance(), amount);
            throw new InsufficientFundsException(String.format("Amount withdrawn attempted overdraft. Balance: $%.2f", account.getBalance()));
        }

        try {
            // Update Balance column in DataBase
            accountRepository.Withdraw_Balance(account, amount);

            // Add transaction history
            transactionHistoryService.attemptAddDepositOrWithdrawal("Withdraw", amount, account.getAccountNumber());

            // 2. The "Happy Path" (INFO)
            log.info("Success: Withdrew ${} from Account {}. New Balance: ${}", amount, String.format("%s ****%04d", account.getAccountType(), (account.getAccountNumber() % 10000)), account.getBalance());

        } catch (Exception e) {
            log.error("System error during withdrawal for Account {}: {}", String.format("%s ****%04d", account.getAccountType(), (account.getAccountNumber() % 10000)), e.getMessage(), e);
            throw e;
        }
    }

    private static void validateMoneyAmount(double amount) {
        if (!Double.isFinite(amount) || !hasAtMostTwoDecimalPlaces(amount)) {
            throw new IllegalArgumentException(
                    "Amount must be a number with no more than 2 decimal places."
            );
        }
    }

    private static boolean hasAtMostTwoDecimalPlaces(double amount) {
        return amount * 100 % 1 == 0;
    }
}
