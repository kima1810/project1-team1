package org.half.view;

import org.half.exceptions.InsufficientFundsException;
import org.half.model.Account;
import org.half.service.AccountService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;

public class Withdraw {
    public static void Withdraw_View(Account currentAccount) {
        boolean running = true;

        while (running) {
            System.out.printf("Current Balance: %s$%.2f%s\n", ANSI.rgb(0, 255, 0), currentAccount.getBalance(), "\033[0m");
            System.out.print("Please enter the amount you would like to withdraw or type 0 to go back to the main menu: ");
            double AmountWithDrawn = BankScanner.getDouble();

            // Exit back to Main Menu
            if (AmountWithDrawn == 0) {
                running = false;
                continue;
            }

            // Withdrawn valid input checker (Added continue to prevent going to confirmation prompt)
            if (AmountWithDrawn < 0) {
                System.out.println("Error: Amount cannot be negative.\n");
                continue;
            }
            if (AmountWithDrawn > currentAccount.getBalance()) {
                System.out.println("Error: Insufficient funds!\n");
                continue;
            }

            // Withdrawn confirmation (Fixed the typo from "Deposit" to "Withdraw")
            System.out.print("Can you confirm that this is the amount you want to Withdraw?: $" + AmountWithDrawn + " (Yes/No) ");
            String Confirmation = BankScanner.getString();

            if (Confirmation.equalsIgnoreCase("Yes")) {
                try {
                    // Attempt the transaction
                    AccountService.Withdraw_Request(currentAccount, AmountWithDrawn);
                    System.out.printf("Congrats! Your withdrawal was successful. Your new Balance is: $%.2f\n", currentAccount.getBalance());
                } catch (IllegalArgumentException | InsufficientFundsException e) {
                    // Catch business rule errors (e.g., negative numbers or over-drafting)
                    System.out.println(e.getMessage() + "\n");
                } catch (Exception e) {
                    // Catch unexpected system/database errors
                    System.out.println("System Error: Could not process withdrawal at this time.\n");
                }
            } else {
                System.out.println("Transaction Cancelled.\n");
            }
        }
    }
}
