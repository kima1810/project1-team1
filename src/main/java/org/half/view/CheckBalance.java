package org.half.view;

import org.half.utility.BankScanner;

public class CheckBalance {
    public static void showBalance() {
        System.out.println("Your balance is $50.00");
        BankScanner.freeze();
    }
}