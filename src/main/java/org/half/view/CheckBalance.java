package org.half.view;

import org.half.utility.BankScanner;
import org.half.model.Account;

public class CheckBalance {
    public static void showBalance(Account account) {
        System.out.printf("Your balance is $%.2f\n",account.getBalance());
        BankScanner.freeze();
    }
}