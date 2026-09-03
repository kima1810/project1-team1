package org.half;

import org.half.utility.BankScanner;
import org.half.view.MainMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Bank 50!");
        System.out.println("Are you a member of our Bank? Yes or No");

        String input = BankScanner.getString().toLowerCase();

        if (input.charAt(0) == 'n' ){
            //Call the Registration Page
        }

        System.out.println("Please enter your Account ID.");
        int userAccountID = BankScanner.getInt();
        //Check to make sure that is a valid Account ID
        System.out.println("Please enter your PIN number.");
        int userPinNum = BankScanner.getInt();
        //Check to make sure that PIN number is correct of the Account ID

        MainMenu.mainMenu();
        BankScanner.closeScanner();
    }
}