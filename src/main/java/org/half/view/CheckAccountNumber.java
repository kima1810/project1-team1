package org.half.view;

import org.half.model.Account;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;

public class CheckAccountNumber {
    public static void showAccountNumber(Account account) {
        System.out.println("\nYour account number is:");
        System.out.println(ANSI.MAGENTA + account.getAccountNumber() + ANSI.RESET);

        // Freeze the screen
        System.out.print(ANSI.rgb(100, 255, 100) + "\nPress enter to continue..." + ANSI.RESET);
        BankScanner.freeze();
    }
}
