package org.half.view;

import org.half.repository.UserRepository;
import org.half.service.UserService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.half.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignIn {
    private static final Logger log = LoggerFactory.getLogger(SignIn.class);

    private static final UserRepository userRepository = new UserRepository();
    private static final UserService userService = new UserService(userRepository);

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
            System.out.println("\n" + ANSI.title(
                    """
                            ┌────────────────────────────────┐
                            │  Welcome to Fifty/50 Bank!     │
                            └────────────────────────────────┘
                            """));

            System.out.println("Are you a member of our Bank? Yes or No");
            System.out.println(ANSI.optionPositive("[1] Yes: Sign In"));
            System.out.println(ANSI.optionPositive("[2] No: Create a New User Account"));
            System.out.println(ANSI.optionNegative("[0] Exit"));

            System.out.println("\n──────────────────────────────────");

            System.out.print("Select an option: ");
            int userInput = BankScanner.promptUserSelection();

            switch (userInput) {
                case 1:
                    // Logging in title
                    System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                            "Let's sign in to your profile..." +
                            ANSI.RESET);

                    while (true) {
                        System.out.println("Please enter your Username.");
                        String userName = BankScanner.getString();

                        System.out.println("Please enter your Password.");
                        String userPassword = BankScanner.getString();
                        if(!userName.isEmpty() && !userPassword.isEmpty()) {
                            User activeUser = userService.verifyUser(userName, userPassword);
                            if (activeUser != null) {
                                System.out.println(ANSI.success("Successfully logged in to your profile..."));
                                log.info("User logged in: userId={}", userName);
                                AccountSelection.selectAccount(activeUser);
                                break;
                            }
                        }

                        System.out.println(ANSI.userWarning("Invalid Credentials. Try again..."));
                        log.warn("Login failed: userId={}", userName);
                    }
                    break;
                case 2:
                    Register.register();
                    break;
                case 0:
                    break exitBank;
                default:
                    System.out.println(ANSI.userWarning("Invalid Option"));
                    log.warn("Entered Invalid Sign In Menu Option");
            }
        }
    }
}
