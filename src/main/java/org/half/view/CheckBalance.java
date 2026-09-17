package org.half.view;

import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.half.model.Account;

public class CheckBalance {
    public static void showBalance(Account account) {
        System.out.printf("Your balance is %s$%.2f%s\n", ANSI.rgb(0, 255, 0), account.getBalance(), "\033[0m");
        System.out.print("Please press Enter to continue...");
        BankScanner.freeze();
    }
}