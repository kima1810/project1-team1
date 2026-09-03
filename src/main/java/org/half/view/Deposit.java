package org.half.view;

import org.half.utility.BankScanner;

public class Deposit {
    public static void Deposit_Request() {
        double Balance = 50.00; //dummy balance for now
        boolean running = true;

        while (running) {
            System.out.print("Please enter the amount you would like to deposit or type 0 to go back to the main menu:");
            double AmountDeposit = BankScanner.getDouble();

            //return to main menu
            if  (AmountDeposit == 0) {
                running = false;
                continue;
            }

            // Deposit valid input checker
            if (AmountDeposit < 0) {
                System.out.println("Amount can't be negative!");
            }

            // Deposit Confirmation Check
            System.out.print("Can you confirm that this is the amount you want to Deposit?: $" + AmountDeposit + " (Yes/No) ");
            String Confirm = BankScanner.getString();
            if (Confirm.equalsIgnoreCase("Yes")) {
                Balance += AmountDeposit;
                System.out.printf("Your new balance is $%.2f\n", Balance);
            } else {
                System.out.println("Transaction Cancelled.");
            }
        }
    }
}
