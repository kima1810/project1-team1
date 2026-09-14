package org.half.view;

import org.half.service.AccountService;
import org.half.utility.BankScanner;
import org.half.model.Account;
import org.half.repository.AccountRepository;

public class Deposit {
    public static void Deposit_View(Account currentAccount) {
        boolean running = true;

        while (running) {
            System.out.printf("Current Balance: $%.2f\n", currentAccount.getBalance());
            System.out.print("Please enter the amount you would like to deposit or type 0 to go back to the main menu: ");
            double AmountDeposit = BankScanner.getDouble();

            // return to main menu
            if (AmountDeposit == 0) {
                running = false;
                continue;
            }

            // Deposit valid input checker (Added continue to stop the loop from proceeding)
            if (AmountDeposit < 0) {
                System.out.println("Error: Amount can't be negative!\n");
                continue;
            }

            // Deposit Confirmation Check
            System.out.print("Can you confirm that this is the amount you want to Deposit?: $" + AmountDeposit + " (Yes/No) ");
            String Confirmation = BankScanner.getString();

            if (Confirmation.equalsIgnoreCase("Yes")) {
                try {
                    // Attempt the transaction
                    AccountService.Deposit_Request(currentAccount, AmountDeposit);
                    System.out.printf("Congrats! Your deposit was successful. Your new Balance is: $%.2f\n\n", currentAccount.getBalance());
                } catch (IllegalArgumentException e) {
                    // Catch validation errors from the service
                    System.out.println(e.getMessage() + "\n");
                } catch (Exception e) {
                    // Catch unexpected errors (like database failures)
                    System.out.println("System Error: Could not process deposit at this time.\n");
                }
            } else {
                System.out.println("Transaction Cancelled.\n");
            }
        }
    }
}
