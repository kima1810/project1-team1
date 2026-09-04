package org.half.view;
import org.half.model.User;

import org.half.utility.BankScanner;

public class MainMenu {

    public static void mainMenu(User activeUser) {
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
                    Transfer.transfer();
                    break;
            }
        }
    }
}
