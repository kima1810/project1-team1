package org.half.service;

import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.half.security.PasswordService;
import org.half.view.TransactionHistory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AccountService {
    public static boolean createAccount(User user, int pin, AccountType accountType) {
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

            return true;
        }

        return false;
    }

    public static List<Account> getAccounts(User user) {
        return AccountRepository.getAllAccounts(user);
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
        TransactionHistory.addTransaction(
                "Transfer",
                amount,
                sourceAccount.getBalance(),
                String.valueOf(destinationAccountNumber)
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
        TransactionHistory.addTransaction("Deposit", amount, account.getBalance(), "N/A");

    }

    public static void Withdraw_Request(Account account, double amount) {
        //Create new Balance
        double NewBalance = account.getBalance() - amount;
        //Update Balance column in DataBase
        AccountRepository.Update_Balance(account, NewBalance);
        //Update current instance of Balance (balance stay updated throughout instance)
        account.setBalance(NewBalance);
        //Now the transaction will be added
        TransactionHistory.addTransaction("Withdraw", amount, account.getBalance(), "N/A");
    }
}
