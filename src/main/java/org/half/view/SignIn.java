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

        while(true){
            System.out.println("Please enter your Username.");
            String userName = BankScanner.getString();

            System.out.println("Please enter your Password.");
            String userPassword = BankScanner.getString();
            String dataBasePassword = UserRepository.getPasswordHash(userName);

            if(dataBasePassword != null){
                if(PasswordService.verifyPassword(userPassword, dataBasePassword)){
                    System.out.println("Successfully Logged In to Your Account");
                    User activeUser = UserRepository.getUser(userName);
                    MainMenu.mainMenu(activeUser);
                    break;
                }
            }


            System.out.println("You Entered Invalid Credentials.");
            System.out.println("Please Reenter your Credentials.");


        }




        //BankScanner.closeScanner();
    }
}
