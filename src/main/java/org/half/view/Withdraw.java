package org.half.view;

import org.half.exceptions.InsufficientFundsException;
import org.half.model.Account;
import org.half.repository.AccountRepository;
import org.half.repository.TransactionModelRepository;
import org.half.service.AccountService;
import org.half.service.TransactionHistoryService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;

public class Withdraw {
    private static final AccountRepository accountRepository = new AccountRepository();
    private static final TransactionModelRepository transactionModelRepository = new TransactionModelRepository();

    private static final TransactionHistoryService transactionHistoryService = new TransactionHistoryService(transactionModelRepository);

    private static final AccountService accountService = new AccountService(accountRepository, transactionHistoryService);

    public static void Withdraw_View(Account currentAccount) {
        boolean running = true;
        System.out.printf("\nCurrent Balance: %s$%.2f%s\n", ANSI.rgb(0, 255, 0), currentAccount.getBalance(), "\033[0m");
        while (running) {
            System.out.printf("Please enter the amount you would like to deposit or type %s0%s to go back to the main menu: ", ANSI.rgb(255, 0, 0), "\033[0m");
            double AmountWithDrawn = BankScanner.getDouble();

            // Exit back to Main Menu
            if (AmountWithDrawn == 0) {
                running = false;
                continue;
            }

            // Withdrawn confirmation (Fixed the typo from "Deposit" to "Withdraw")
            System.out.printf("Can you confirm that this is the amount you want to Withdraw?: %s$%.2f%s (Yes/No) ", ANSI.rgb(0, 255, 0), AmountWithDrawn, "\033[0m");
            String Confirmation = BankScanner.getString();

            if (Confirmation.equalsIgnoreCase("Yes")) {
                try {
                    // Attempt the transaction
                    accountService.Withdraw_Request(currentAccount, AmountWithDrawn);
                    System.out.printf("Congrats! Your withdrawal was successful. Your new Balance is: %s$%.2f%s\n\n", ANSI.rgb(0, 255, 0), currentAccount.getBalance(), "\033[0m");
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
