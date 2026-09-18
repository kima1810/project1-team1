package org.half.service;

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
    public long createAccount(User user, int pin, AccountType accountType) {
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
                || amount > sourceAccount.getBalance()
                || sourceAccount.getAccountNumber() == destinationAccountNumber) {
            return false;
        }

        if (!accountRepository.transferFunds(sourceAccount, destinationAccountNumber, amount)) {
            return false;
        }

        
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        //
        transactionHistoryService.attemptAddTransfer(
                "Transfer",
                amount,
                sourceAccount.getAccountNumber(),
                destinationAccountNumber
        );
        return true;
    }

    public void Deposit_Request(Account account, double amount) {
        //Negative value check
        if (amount < 0) {
            log.warn("Failed deposit attempt: Account {} entered a negative amount (${}).", account.getAccountNumber(), amount);
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        try {
            //Create new Balance
            double NewBalance = account.getBalance() + amount;
            //Update Balance column in DataBase
            accountRepository.Update_Balance(account, NewBalance);
            //Update current instance of Balance (balance stay updated throughout instance)
            account.setBalance(NewBalance);
            //Now the transaction will be added
            transactionHistoryService.attemptAddDepositOrWithdrawal("Deposit", amount, account.getAccountNumber());
        } catch (Exception e) {
            log.error("System error during deposit for Account {}: {}", account.getAccountNumber(), e.getMessage(), e);
            throw e;
        }
    }

    public void Withdraw_Request(Account account, double amount) {
        //Negative value check
        if (amount < 0) {
            log.warn("Failed Withdraw attempt: Account {} entered a negative amount (${}).", account.getAccountNumber(), amount);
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        //Overdraft
        if (account.getBalance() < amount) {
            log.warn("Failed withdrawal attempt: Account {} attempted overdraft. Balance: ${}, Attempted: ${}", account.getAccountNumber(), account.getBalance(), amount);
            throw new InsufficientFundsException(String.format("Amount withdrawn attempted overdraft. Balance: $%.2f", account.getBalance()));
        }

        try {
            // Create new Balance
            double NewBalance = account.getBalance() - amount;

            // Update Balance column in DataBase
            accountRepository.Update_Balance(account, NewBalance);

            // Update current instance of Balance
            account.setBalance(NewBalance);

            // Add transaction history
            transactionHistoryService.attemptAddDepositOrWithdrawal("Withdraw", amount, account.getAccountNumber());

            // 2. The "Happy Path" (INFO)
            log.info("Success: Withdrew ${} from Account {}. New Balance: ${}", amount, account.getAccountNumber(), NewBalance);

        } catch (Exception e) {
            log.error("System error during withdrawal for Account {}: {}", account.getAccountNumber(), e.getMessage(), e);
            throw e;
        }
    }
}
