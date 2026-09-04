package org.half.model;

public class User {
    // possible things to add: Real Name, address, phone number, email, routing number? But not necessary right now.
    private int id;
    private String pin;
    private double balance;

    public User() {
        // fill in later
    }

    public User(int id, String pin, double balance) {
        this.id = id;
        this.pin = pin;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        // validate pin?
        this.pin = pin;
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
}