package org.half.view;

import org.half.repository.UserRepository;
import org.half.utility.BankScanner;
import org.half.model.User;
import org.half.security.PasswordService;

public class SignIn {
    public static void signIn(){
        System.out.println("Welcome to Bank 50!");
        System.out.println("Are you a member of our Bank? Yes or No");

        String input = BankScanner.getString().toLowerCase();

        while(input == null || (!input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no"))){
            System.out.println("Invalid Response: Please enter Yes or No");
            input = BankScanner.getString().toLowerCase();
        }

        if (input.charAt(0) == 'n' ){
            Register.register();
        }
        System.out.println("Please enter your Account ID.");
        String userName = BankScanner.getString();
        while (!UserRepository.checkUser(userName)){
            System.out.println("Invalid Username: Please Reenter your Username");
            userName = BankScanner.getString();
        }
        User activeUser = UserRepository.getUser(userName);
        System.out.println("Please enter your Password.");
        String userPassword = BankScanner.getString();
        while(!PasswordService.verifyPassword(userPassword, activeUser.password)){
            System.out.println("Entered Wrong Password For Your Account: Reenter Your Password");
            userPassword = BankScanner.getString();
        }
        MainMenu.mainMenu(activeUser);


        //BankScanner.closeScanner();
    }
}
