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
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public static long createAccount(User user, int pin, AccountType accountType) {
        if (pin > 9999) {
            throw new IllegalArgumentException("Invalid pin. Cannot be more than 4 digits.");
        }
        String pinHash = PasswordService.hashPassword(String.valueOf(pin));

        for (int i = 0; i < 10; i++) {
            long accountNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);

            Account account = new Account(user, accountNumber, pinHash, accountType, 0.00);

            try {
                AccountRepository.addAccount(account);
            } catch (SQLException e) {
                String message = e.getMessage();
                if (message != null && message.contains("[SQLITE_CONSTRAINT_PRIMARYKEY]")) {
                    System.out.println("[WARN - Custom] Account number already in use. Generating a new one...");
                } else {
                    System.out.println(message);
                }
                continue;
            }

            return accountNumber;
        }

        return -1;
    }

    public static List<Account> getAccounts(User user) {
        try {
            return AccountRepository.getAllAccounts(user);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
        //Negative value check
        if (amount < 0) {
            logger.warn("Failed deposit attempt: Account {} entered a negative amount (${}).", account.getAccountNumber(), amount);
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        try {
            //Create new Balance
            double NewBalance = account.getBalance() + amount;
            //Update Balance column in DataBase
            AccountRepository.Update_Balance(account, NewBalance);
            //Update current instance of Balance (balance stay updated throughout instance)
            account.setBalance(NewBalance);
            //Now the transaction will be added
            TransactionHistoryService.attemptAddDepositOrWithdrawal("Deposit", amount, account.getAccountNumber());
        } catch (Exception e) {
            logger.error("System error during deposit for Account {}: {}", account.getAccountNumber(), e.getMessage(), e);
            throw e;
        }
    }

    public static void Withdraw_Request(Account account, double amount) {
        //Negative value check
        if (amount < 0) {
            logger.warn("Failed deposit attempt: Account {} entered a negative amount (${}).", account.getAccountNumber(), amount);
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        //Overdraft
        if (account.getBalance() < amount) {
            logger.warn("Failed withdrawal attempt: Account {} attempted overdraft. Balance: ${}, Attempted: ${}", account.getAccountNumber(), account.getBalance(), amount);
            throw new InsufficientFundsException("Account balance cannot be less than amount.");
        }

        try {
            // Create new Balance
            double NewBalance = account.getBalance() - amount;

            // Update Balance column in DataBase
            AccountRepository.Update_Balance(account, NewBalance);

            // Update current instance of Balance
            account.setBalance(NewBalance);

            // Add transaction history
            TransactionHistoryService.attemptAddDepositOrWithdrawal("Withdraw", amount, account.getAccountNumber());

            // 2. The "Happy Path" (INFO)
            logger.info("Success: Withdrew ${} from Account {}. New Balance: ${}", amount, account.getAccountNumber(), NewBalance);

        } catch (Exception e) {
            logger.error("System error during withdrawal for Account {}: {}", account.getAccountNumber(), e.getMessage(), e);
            throw e;
        }
    }
}
