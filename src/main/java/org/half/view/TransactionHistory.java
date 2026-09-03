package org.half.view;
import org.half.utility.BankScanner;

import java.util.ArrayList;

public class TransactionHistory {
    static class Transaction {
        String type;
        double amount;
        double currentBalance;
        String accountSentTo;


        public Transaction(String type, double amount, double balance, String accountSentTo) {
            this.type = type;
            this.amount = amount;
            this.currentBalance = balance;
            this.accountSentTo = accountSentTo;
        }


        @Override
        public String toString() {
            return "Type: " + type +
                    ", Amount: " + amount +
                    ", Balance: " + currentBalance +
                    ", Account Sent To: " + accountSentTo;
        }
    }

    //this holds all the transactions
    private static ArrayList<Transaction> transactions = new ArrayList<>();


    //if the transaction is not a transfer, then accountSentTo should be marked as N/A or something like that
    //for the balance part, the getBalance() method should be called
    public static void addTransaction(String type, double amount, double currentBalance, String accountSentTo){
        Transaction transaction = new Transaction(type, amount, currentBalance, accountSentTo);
        transactions.add(transaction);
    }

    //prints the transactions
    public static void printTransactions(){
        System.out.println("Here are your transactions:");
        for (Transaction transaction : transactions){
            System.out.println(transaction.toString());
        }
        BankScanner.freeze();
    }

    //just for testing
/*
    public static void main (String[] args){
        TransactionHistory.addTransaction("deposit", 100.00, Balance.getBalance(), "N/A");
        TransactionHistory.addTransaction("transfer", 50.00, Balance.getBalance(), "randomAccount");

        TransactionHistory.printTransactions();

    }
   */

}

