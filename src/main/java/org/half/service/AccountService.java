package org.half.service;

import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.half.security.PasswordService;
import org.half.service.TransactionHistoryService;

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
