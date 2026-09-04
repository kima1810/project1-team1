package org.half.view;

import org.half.utility.BankScanner;

public class Transfer {
    // Prompt for the details of a transfer between two accounts
    public static void transfer() {
        System.out.print("account to transfer from: ");
        String fromAccount = BankScanner.getString();

        System.out.print("account to transfer to: ");
        String toAccount = BankScanner.getString();

        System.out.print("amount to transfer: ");
        String amount = BankScanner.getString();

        System.out.println("Transfer initiated from " + fromAccount + " to " + toAccount + " for amount " + amount);
        BankScanner.freeze();
    }
}
