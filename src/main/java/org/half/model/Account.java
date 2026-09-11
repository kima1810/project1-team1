package org.half.model;
import org.half.model.enums.AccountType;

import java.util.concurrent.ThreadLocalRandom;

public class Account {
    private User user;
    private long accountNumber;
    private int pin;
    private AccountType accountType;
    private double balance;

//    public Account(User user, int pin, AccountType accountType) {
//        this.user = user;
//
//        // Generate a 12-digit account number
//        do {
//            long accountNumber = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
//        } while (isAccountNumberUnique);
//
//        this.pin = pin;
//        this.accountType = accountType;
//        this.balance = 0.00;
//    }

    public Account(User user, long accountNumber, int pin, AccountType accountType, double balance) {
        this.user = user;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.accountType = accountType;
        this.balance = balance;
    }
}
