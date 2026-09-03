package org.half.view;

import org.half.utility.BankScanner;

public class SignIn {
    public static void signIn(){
        System.out.println("Welcome to Bank 50!");
        System.out.println("Are you a member of our Bank? Yes or No");

        String input = BankScanner.getString().toLowerCase();

        if (input.charAt(0) == 'n' ){
            //Call the Registration Page
        }
        else {
            System.out.println("Please enter your Account ID.");
            int userAccountID = BankScanner.getInt();
            //Check to make sure that is a valid Account ID
            System.out.println("Please enter your PIN number.");
            int userPinNum = BankScanner.getInt();
            //Check to make sure that PIN number is correct of the Account ID
            MainMenu.mainMenu();
        }


        //BankScanner.closeScanner();
    }
}
