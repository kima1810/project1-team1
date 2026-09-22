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
        System.out.println("\nCurrent balance: " + ANSI.success(String.format("$%.2f", currentAccount.getBalance())));
        while (running) {
            System.out.print("Please enter the " + ANSI.optionPositive("withdraw amount") + " or type "+ ANSI.optionNegative("0") +" to return back: ");
            double AmountWithDrawn = BankScanner.getDouble();

            // Exit back to Main Menu
            if (AmountWithDrawn == 0) {
                running = false;
                continue;
            }

            // Withdrawn confirmation (Fixed the typo from "Deposit" to "Withdraw")
            System.out.print("Can you confirm that this is the amount you want to Withdraw?: " +
                    ANSI.success(String.format("$%.2f",AmountWithDrawn)) + " (" +
                    ANSI.optionPositive("Yes") + "/" +
                    ANSI.optionNegative("No") + ") ");
            String Confirmation = BankScanner.getString();

            if (Confirmation.equalsIgnoreCase("Yes")) {
                try {
                    // Attempt the transaction
                    accountService.Withdraw_Request(currentAccount, AmountWithDrawn);
                    System.out.println(ANSI.success("\nCongrats! Your withdrawal was successful."));
                    System.out.println("Your new Balance is: " + ANSI.success(String.format("$%.2f", currentAccount.getBalance())));
                } catch (IllegalArgumentException | InsufficientFundsException e) {
                    // Catch business rule errors (e.g., negative numbers or over-drafting)
                    System.out.println(e.getMessage() + "\n");
                } catch (Exception e) {
                    // Catch unexpected system/database errors
                    System.out.println(ANSI.userWarning("System Error: Could not process withdrawal at this time.\n"));
                }
            } else {
                System.out.println(ANSI.userWarning("Transaction Cancelled.\n"));
            }
        }
    }
}
