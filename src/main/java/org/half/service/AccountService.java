package org.half.service;

import org.half.exceptions.InsufficientFundsException;
import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.half.security.PasswordService;
import org.half.view.TransactionHistory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AccountService {
    public static void createAccount(User user, int pin, AccountType accountType) {
        long accountNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        String pinHash = PasswordService.hashPassword(String.valueOf(pin));
        Account account = new Account(user, accountNumber, pinHash, accountType, 0.00);

        AccountRepository.addAccount(account);

    }

    public static List<Account> getAccounts(User user) {
        return AccountRepository.getAllAccounts(user);
    }

    public static void Deposit_Request(Account account, double amount) throws IllegalArgumentException {
        if (account == null) {
            throw new IllegalArgumentException("Error: No account found.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Error: Deposit amount must be greater than zero.");
        }

        double NewBalance = account.getBalance() + amount;

        //Update Database,local variable, and Transaction History balance value
        AccountRepository.Update_Balance(account, NewBalance);
        account.setBalance(NewBalance);
        TransactionHistory.addTransaction("Deposit", amount, account.getBalance(), "N/A");
    }

    public static void Withdraw_Request(Account account, double amount) throws IllegalArgumentException, InsufficientFundsException {
        if (account == null) {
            throw new IllegalArgumentException("Error: No account found.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Error: Withdrawal amount must be greater than zero.");
        }
        if (amount > account.getBalance()) {
            throw new InsufficientFundsException("Error: Insufficient funds. Your current balance is $%.2f\n" + account.getBalance());
        }

        double NewBalance = account.getBalance() - amount;

        //Update Database,local variable, and Transaction History balance value
        AccountRepository.Update_Balance(account, NewBalance);
        account.setBalance(NewBalance);
        TransactionHistory.addTransaction("Withdraw", amount, account.getBalance(), "N/A");
    }
}
