package org.half.view;

import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.half.model.Account;

public class CheckBalance {
    public static void showBalance(Account account) {
        System.out.printf("\nCurrent balance: %s$%.2f%s\n", ANSI.rgb(0, 255, 0), account.getBalance(), ANSI.RESET);

        // Freeze the screen
        System.out.print(ANSI.rgb(100, 255, 100) + "\nPress enter to continue..." + ANSI.RESET);
        BankScanner.freeze();
    }
}