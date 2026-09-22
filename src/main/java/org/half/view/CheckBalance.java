package org.half.view;

import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.half.model.Account;

public class CheckBalance {
    public static void showBalance(Account account) {
        System.out.println("\nCurrent balance: " + ANSI.success(String.format("$%.2f", account.getBalance())));

        // Freeze the screen
        System.out.print(ANSI.rgb(100, 255, 100) + "\nPress enter to continue..." + ANSI.RESET);
        BankScanner.freeze();
    }
}