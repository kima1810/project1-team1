package org.half.view;

import org.half.repository.UserRepository;
import org.half.utility.BankScanner;
import org.half.model.User;
import org.half.security.PasswordService;

public class SignIn {
    public static void signIn(){
        while(true){
            System.out.println("Welcome to Bank 50!");
            System.out.println("Are you a member of our Bank? Yes or No");

            String input = BankScanner.getString().toLowerCase();

            if (input.equals("0")) break;

            while(input == null || (!input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no"))){
                System.out.println("Invalid Response: Please enter Yes or No");
                input = BankScanner.getString().toLowerCase();
            }

            if (input.charAt(0) == 'n' ){
                Register.register();
            }

            while (true) {
                System.out.println("Please enter your Username.");
                String userName = BankScanner.getString();

                System.out.println("Please enter your Password.");
                String userPassword = BankScanner.getString();
                String dataBasePassword = UserRepository.getPasswordHash(userName);

                if (dataBasePassword != null) {
                    if (PasswordService.verifyPassword(userPassword, dataBasePassword)) {
                        System.out.println("Successfully Logged In to Your Account");
                        User activeUser = UserRepository.getUser(userName);
                        AccountSelection.selectAccount(activeUser);
                        break;
                    }
                }

                System.out.println("Invalid Credentials. Try again...");
            }
        }
    }
}
