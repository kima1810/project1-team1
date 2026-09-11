package org.half.model;
import org.half.model.enums.AccountType;

public class Account {
    private User user;
    private int accountId;
    private int PIN;
    private double balance;
    private AccountType type;

    /* --- Constructors --- */
    public Account() {
        // TODO
    }

    public Account(User user, AccountType type, int pin) {
        this.user = user;
        this.type = type;
        this.PIN = pin;
        this.balance = 0.00;
    }

    public Account(User user, AccountType type, int pin, double balance) {
        this.user = user;
        this.type = type;
        this.PIN = pin;
        this.balance = balance;
    }
    
    /* --- Getters and Setters --- */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getPIN() {
        return PIN;
    }

    public void setPIN(int PIN) {
        this.PIN = PIN;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        this.balance = balance;
    }
    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }
}
