package org.half.model;

public class User {
    // possible things to add: Real Name, address, phone number, email, routing number? But not necessary right now.
    // also consider giving each user a UUID

    // REMINDER: change public variables to private later
    public String firstName;
    public String lastName;
    public String email;
    public long phoneNumber;
    public String username;
    public String password;
    public double balance;

    public User() {
        // fill in later
    }

    public User(String firstName, String lastName, String email, long phoneNumber, String username, String password, double balance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // validate password?
        this.password = password;
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