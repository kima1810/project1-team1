package org.half.view;

import org.half.repository.TransactionModelRepository;
import org.half.service.AccountService;
import org.half.service.TransactionHistoryService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.half.model.Account;
import org.half.repository.AccountRepository;

public class Deposit {
    private static final AccountRepository accountRepository = new AccountRepository();
    private static final TransactionModelRepository transactionModelRepository = new TransactionModelRepository();

    private static final TransactionHistoryService transactionHistoryService = new TransactionHistoryService(transactionModelRepository);

    private static final AccountService accountService = new AccountService(accountRepository, transactionHistoryService);

    public static void Deposit_View(Account currentAccount) {
        boolean running = true;
        System.out.println("\nCurrent balance: " + ANSI.success(String.format("$%.2f", currentAccount.getBalance())));
        while (running) {
            System.out.print("Please enter the " + ANSI.optionPositive("deposit amount") + " or type "+ ANSI.optionNegative("0") +" to return back: ");
            double AmountDeposit = BankScanner.getDouble();

            // return to main menu
            if (AmountDeposit == 0) {
                running = false;
                continue;
            }

            // Deposit Confirmation Check
            System.out.printf("Can you confirm that this is the amount you want to Deposit?: %s$%.2f%s (Yes/No) ", ANSI.rgb(0, 255, 0), AmountDeposit, "\033[0m");
            String Confirmation = BankScanner.getString();

            if (Confirmation.equalsIgnoreCase("Yes")) {
                try {
                    // Attempt the transaction
                    accountService.Deposit_Request(currentAccount, AmountDeposit);
                    System.out.printf("Congrats! Your deposit was successful. Your new Balance is: %s$%.2f%s\n\n", ANSI.rgb(0, 255, 0), currentAccount.getBalance(), "\033[0m");
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
