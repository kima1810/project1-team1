package org.half.model;
import java.util.ArrayList;
import org.half.model.enums.AccountType;
import org.half.model.Account;

public class User {
    // possible things to add: Real Name, address, phone number, email, routing number? But not necessary right now.
    // also consider giving each user a UUID

    private String firstName;
    private String lastName;
    private String email;
    private long phoneNumber; // TODO: Change to String later
    private String username;
    private String password;
    private ArrayList<Account> accounts;

    /* --- Constructors --- */
    public User() {
        // TODO
    }

    public User(String firstName, String lastName, String email, long phoneNumber, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    /* --- Methods --- */
    public void addAccount(AccountType type, int pin) {
        this.accounts.add(new Account(this, type, pin));
    }

    /* --- Getters and Setters --- */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }
}