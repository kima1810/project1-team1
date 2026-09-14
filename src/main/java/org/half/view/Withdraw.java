package org.half.view;

import org.half.model.Account;
import org.half.service.AccountService;
import org.half.utility.BankScanner;

public class Withdraw {
    public static void Withdraw_View(Account currentAccount) {
        boolean running = true;
        while (running) {
            System.out.printf("Current Balace: $%.2f\n", currentAccount.getBalance());
            System.out.print("Please enter the amount you would like to withdraw or type 0 to go back to the main menu: ");
            double AmountWithDrawn = BankScanner.getDouble();

            //Exit back to Main Menu
            if (AmountWithDrawn == 0) {
                running = false;
                continue;
            }

            //Withdrawn valid input checker
            if (AmountWithDrawn < 0 || AmountWithDrawn > currentAccount.getBalance()) {
                System.out.println("Error: Invalid Amount!");
                continue;
            }

            //Withdrawn confirmation
            System.out.print("Can you confirm that this is the amount you want to Deposit?: $" + AmountWithDrawn + " (Yes/No) ");
            String Confirmation = BankScanner.getString();
            if (Confirmation.equalsIgnoreCase("Yes")) {
                AccountService.Withdraw_Request(currentAccount, AmountWithDrawn);
                System.out.printf("Congrats! Your withdrawal was successful. Your new Balance is: $%.2f\n", currentAccount.getBalance());
            } else {
                System.out.println("Transaction Cancelled.");
            }
        }
    }
}
