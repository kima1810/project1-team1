package org.half.model;
import org.half.model.enums.AccountType;

public class Account {
    private User user;
    private long accountNumber;
    private String pinHash;
    private AccountType accountType;
    private double balance;

    public Account(User user, long accountNumber, String pinHash, AccountType accountType, double balance) {
        this.user = user;
        this.accountNumber = accountNumber;
        this.pinHash = pinHash;
        this.accountType = accountType;
        this.balance = balance;
    }

    public double getBalance() {
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
}
