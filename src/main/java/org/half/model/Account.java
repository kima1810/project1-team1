package org.half.model;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;

public class Account {
    private final User user;
    private final long accountNumber;
    private final String pinHash;
    private final AccountType accountType;
    private double balance;

    public Account(User user, long accountNumber, String pinHash, AccountType accountType, double balance) {
        this.user = user;
        this.accountNumber = accountNumber;
        this.pinHash = pinHash;
        this.accountType = accountType;
        this.balance = balance;
    }

    public double getBalance() {
        AccountRepository.getBalance(accountNumber)
                .ifPresent(databaseBalance -> this.balance = databaseBalance);
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getPinHash() {
        return pinHash;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public User getUser() {
        return user;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
