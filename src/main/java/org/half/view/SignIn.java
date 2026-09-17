package org.half.view;

import org.half.security.SignInService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.half.model.User;

public class SignIn {
    public static void signIn(){
        System.out.println(ANSI.rgb(0, 255, 0) +
                " /$$$$$$$$ /$$  /$$$$$$   /$$                     /$$ /$$$$$$$   /$$$$$$        /$$$$$$$                      /$$      \n" +
                "| $$_____/|__/ /$$__  $$ | $$                    /$$/| $$____/  /$$$_  $$      | $$__  $$                    | $$      \n" +
                "| $$       /$$| $$  \\__//$$$$$$   /$$   /$$     /$$/ | $$      | $$$$\\ $$      | $$  \\ $$  /$$$$$$  /$$$$$$$ | $$   /$$\n" +
                "| $$$$$   | $$| $$$$   |_  $$_/  | $$  | $$    /$$/  | $$$$$$$ | $$ $$ $$      | $$$$$$$  |____  $$| $$__  $$| $$  /$$/\n" +
                "| $$__/   | $$| $$_/     | $$    | $$  | $$   /$$/   |_____  $$| $$\\ $$$$      | $$__  $$  /$$$$$$$| $$  \\ $$| $$$$$$/ \n" +
                "| $$      | $$| $$       | $$ /$$| $$  | $$  /$$/     /$$  \\ $$| $$ \\ $$$      | $$  \\ $$ /$$__  $$| $$  | $$| $$_  $$ \n" +
                "| $$      | $$| $$       |  $$$$/|  $$$$$$$ /$$/     |  $$$$$$/|  $$$$$$/      | $$$$$$$/|  $$$$$$$| $$  | $$| $$ \\  $$\n" +
                "|__/      |__/|__/        \\___/   \\____  $$|__/       \\______/  \\______/       |_______/  \\_______/|__/  |__/|__/  \\__/\n" +
                "                                  /$$  | $$                                                                            \n" +
                "                                 |  $$$$$$/                                                                            \n" +
                "                                  \\______/                                                                             " +
                ANSI.RESET);
        exitBank:
        while(true){
            System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                    "┌────────────────────────────────┐\n" +
                    "│  Welcome to Bank 50!           │\n" +
                    "└────────────────────────────────┘\n" +
                    ANSI.RESET
            );
            System.out.print(ANSI.rgb(100, 255, 100));
            System.out.println("Are you a member of our Bank? Yes or No");
            System.out.println("[1] Yes: Sign In");
            System.out.println("[2] No: Create a New User Account");
            System.out.println(ANSI.rgb(255,100,100) + "[0] Exit");
            System.out.print(ANSI.RESET);

            System.out.println("\n──────────────────────────────────");

            System.out.print("Select an option: ");
            int userInput = BankScanner.promptUserSelection();

            switch (userInput) {
                case 1:
                    while (true) {
                        System.out.println("Please enter your Username.");
                        String userName = BankScanner.getString();

                        System.out.println("Please enter your Password.");
                        String userPassword = BankScanner.getString();
                        if(!userName.isEmpty() && !userPassword.isEmpty()) {
                            User activeUser = SignInService.verifyUser(userName, userPassword);
                            if (activeUser != null) {
                                System.out.println("Successfully Logged In to Your Account");
                                AccountSelection.selectAccount(activeUser);
                                break;
                            }
                        }

                        System.out.println("Invalid Credentials. Try again...");
                    }
                    break;
                case 2:
                    Register.register();
                    break;
                case 0:
                    break exitBank;
                default:
                    System.out.println("Invalid Option");
            }
        }
    }
}
