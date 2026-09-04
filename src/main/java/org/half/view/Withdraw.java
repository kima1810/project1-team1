package org.half.view;

import org.half.utility.BankScanner;

public class Withdraw {
    public static void Withdraw_Request() {
        double Balance = 50.00; //dummy balance for now
        boolean running = true;
        while (running) {
            System.out.print("Please enter the amount you would like to withdraw or type 0 to go back to the main menu: ");
            double AmountWithDrawn = BankScanner.getDouble();

            //Exit back to Main Menu
            if (AmountWithDrawn == 0) {
                running = false;
                continue;
            }

            //Withdrawn valid input checker
            if (AmountWithDrawn < 0 || AmountWithDrawn > Balance) {
                System.out.println("Invalid Amount!");
            }

            //Withdrawn confirmation
            System.out.print("Can you confirm that this is the amount you want to windraw?: $" + AmountWithDrawn + " (Yes/No) ");
            String Confirm = BankScanner.getString();
            if (Confirm.equalsIgnoreCase("Yes")) {
                Balance -= AmountWithDrawn;
                //will be added to the transactions
                TransactionHistory.addTransaction("withdraw", AmountWithDrawn, 50.00, "N/A");
                System.out.printf("Your remaining balance is $%.2f\n", Balance);
            } else {
                System.out.println("Transaction Cancelled.");
            }
        }
    }
}
