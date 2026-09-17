package org.half.view;

import org.half.model.Account;
import org.half.model.User;
import org.half.service.AccountService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// Account selection view
public class AccountSelection {
    // Class specific Logger for logging
    private static final Logger log = LoggerFactory.getLogger(AccountSelection.class);

    // View for user to select their account
    public static void selectAccount(User user) {
        // Welcome the user
        System.out.println(ANSI.MAGENTA + ANSI.HIGH_INTENSITY + "\nWelcome, " +
                ANSI.CYAN + ANSI.ITALIC + user.getUsername() + ANSI.RESET +
                ANSI.MAGENTA + ANSI.HIGH_INTENSITY + "!" + ANSI.RESET);

        // Show the account selection menu
        while (true) {
            // Get all the accounts for the logged-in user
            List<Account> accounts = AccountService.getAccounts(user);

            // If user has no account, keep prompting them to create a new account
            while (accounts == null) {
                System.out.println("No accounts found.");

                // Call the account creation view
                AccountCreation.createAccount(user);

                // Retry getting accounts
                accounts = AccountService.getAccounts(user);
            }

            // Selecting accounts title
            System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                    "┌────────────────────────────────┐\n" +
                    "│  Your Accounts:                │\n" +
                    "└────────────────────────────────┘\n" +
                    ANSI.RESET
            );

            log.info("Printing all the accounts for user: {}", user.getUsername());

            // Print each account and their options
            System.out.print(ANSI.rgb(100, 255, 100));
            for (int i = 1; i <= accounts.size(); i++) {
                Account account = accounts.get(i - 1);
                System.out.printf("[" + i + "] " + account.getAccountType() + " ****%04d%n",(account.getAccountNumber() % 10000));
            }

            // Print other options
            System.out.println(ANSI.rgb(50, 245, 245) + "[-1] Open a new account.");
            System.out.println(ANSI.rgb(255,125,100) + "[0] Logout");

            System.out.print(ANSI.RESET);
            System.out.println("\n──────────────────────────────────");

            // Ask user to select an option
            System.out.print("Please select your account: ");
            int accountSelected;
            while (true) {
                // Prompt user to input an option
                accountSelected = BankScanner.promptUserSelection();

                // Check if a valid option was selected
                if (accountSelected > accounts.size()) {
                    System.out.println("Please enter a number between 1 and " + accounts.size());
                    continue;
                }

                // Correct option inputted
                break;

            }

            log.info("User selected option: {}", accountSelected);

            // Check if user wants to log out
            if (accountSelected == 0) {
                // Log out the user
                log.info("Logging out user: {}", user.getUsername());
                break;
            }

            // Check if the user wants to create a new account
            else if (accountSelected == -1) {
                // Call the account creation view
                AccountCreation.createAccount(user);
                continue;
            }

            // Call the main menu view
            MainMenu.mainMenu(accounts.get(accountSelected - 1));
        }

    }
}
