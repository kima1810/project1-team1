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

            int userInput = BankScanner.getInt();

            switch (userInput) {
                case 1:
                    CheckBalance.showBalance();
                    break;
                case 0:
                    running = false;
                    break;
            }
        }
    }
}
