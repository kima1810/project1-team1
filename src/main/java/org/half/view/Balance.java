package org.half.view;

import org.half.utility.BankScanner;

public class Balance {
    private static double mainBalance = 500.00;

    public static double getBalance(){
        return mainBalance;
    }

    public static void showBalance() {
        System.out.println("Your balance is $" + getBalance());
        BankScanner.freeze();
    }

    //just for testing
    /*
    public static void main (String[] args){
        showBalance();
    }
    */

}
