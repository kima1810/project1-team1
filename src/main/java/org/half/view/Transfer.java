package org.half.view;

import org.half.model.Account;
import org.half.repository.AccountRepository;
import org.half.service.AccountService;
import org.half.utility.BankScanner;

public class Transfer {
    private static final AccountRepository accountRepository = new AccountRepository();

    private static final AccountService accountService = new AccountService(accountRepository);

    public static void transfer(Account sourceAccount) {
        System.out.printf("\nCurrent balance: $%.2f%n", sourceAccount.getBalance());
        System.out.print("Destination account number (or 0 to cancel): ");
        String destinationInput = BankScanner.getString();

        if (destinationInput.equals("0")) {
            return;
        }

        long destinationAccountNumber;
        try {
            destinationAccountNumber = Long.parseLong(destinationInput);
        } catch (NumberFormatException exception) {
            System.out.println("Account number destination is invalid.");
            BankScanner.freeze();
            return;
        }

        if (destinationAccountNumber == sourceAccount.getAccountNumber()) {
            System.out.println("You can't transfer money to the same account.");
            BankScanner.freeze();
            return;
        }

        if (!accountService.accountExists(destinationAccountNumber)) {
            System.out.println("Destination account not found.");
            BankScanner.freeze();
            return;
        }

        System.out.print("Amount to transfer: $");
        double amount;
        try {
            amount = Double.parseDouble(BankScanner.getString());
        } catch (NumberFormatException exception) {
            System.out.println("Invalid transfer amount.");
            BankScanner.freeze();
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            System.out.println("Transfer amount must be greater than zero.");
            BankScanner.freeze();
            return;
        }

        if (amount > sourceAccount.getBalance()) {
            System.out.println("Insufficient funds.");
            BankScanner.freeze();
            return;
        }

        System.out.printf(
                "Transfer $%.2f to account ending in %04d? (Yes/No) ",
                amount,
                destinationAccountNumber % 10000
        );
        String confirmation = BankScanner.getString();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Transfer cancelled.");
            BankScanner.freeze();
            return;
        }

        if (accountService.transfer(sourceAccount, destinationAccountNumber, amount)) {
            System.out.printf(
                    "Transfer successful. Your new balance is $%.2f%n",
                    sourceAccount.getBalance()
            );
        } else {
            System.out.println("Transfer failed. Your balance was not changed.");
        }

        BankScanner.freeze();
    }
}
