package org.half.view;

import org.half.utility.BankScanner;

public class MainMenu {

    public static void mainMenu() {
        boolean running = true;

        while (running) {
            System.out.println("Welcome, John Doe!");
            System.out.println("1. View balance");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transaction history");
            System.out.println("5. Transfer money");

            int userInput = BankScanner.getInt();

            switch (userInput) {
                case 3:
                    Deposit.Deposit_Request();
                    break;
                case 2:
                    Withdraw.Withdraw_Request();
                    break;
                case 1:
                    CheckBalance.showBalance();
                    break;
                case 0:
                    running = false;
                    BankScanner.closeScanner();
                    break;
                case 4:
                    TransactionHistory.printTransactions();
                    break;
                case 5:
                    transfer();
                    break;
            }
        }
    }

    // Prompt for the details of a transfer between two accounts
    private static void transfer() {
        System.out.print("account to transfer from: ");
        String fromAccount = BankScanner.getString();

        System.out.print("account to transfer to: ");
        String toAccount = BankScanner.getString();

        System.out.print("amount to transfer: ");
        String amount = BankScanner.getString();

        System.out.println("Transfer initiated from " + fromAccount + " to " + toAccount + " for amount " + amount);
        BankScanner.freeze();
    }
}
