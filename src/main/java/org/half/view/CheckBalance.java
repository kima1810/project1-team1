package org.half.view;

import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.half.model.Account;

public class CheckBalance {
    public static void showBalance(Account account) {
        System.out.printf("Your balance is $%.2f\n",account.getBalance());

        // Freeze the screen
        System.out.print(ANSI.rgb(100, 255, 100) + "\nPress enter to continue..." + ANSI.RESET);
        BankScanner.freeze();
    }
}