package org.half.view;

import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.half.service.AccountService;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Account creation view
public class AccountCreation {
    // Class specific Logger for logging
    private static final Logger log = LoggerFactory.getLogger(AccountCreation.class);

    private static final AccountRepository accountRepository = new AccountRepository();

    private static final AccountService accountService = new AccountService(accountRepository);

    // View for user to create a new account
    public static void createAccount(User user) {
        // Creating new account title
        System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                "Let's create your account..." +
                ANSI.RESET);

        log.info("Creating a new account for user: {}", user.getUsername());

        // Ask user to choose an account type
        while (true) {
            System.out.println("Choose account type: " +
                    ANSI.rgb(100, 255, 100) + "CHECKING" +
                    ANSI.RESET + " or " +
                    ANSI.rgb(100, 255, 100) + "SAVINGS" + ANSI.RESET);

            // Prompt user for account type
            AccountType accountType;
            while (true) {
                // Get string input from user
                String accountTypeInput = BankScanner.getString();

                try {
                    // Cast user input to account type enum
                    accountType = AccountType.valueOf(accountTypeInput.toUpperCase());
                    log.info("User selected account type: {}", accountType);
                    break;
                } catch (IllegalArgumentException e) {
                    // Invalid input account type entered, ask user to try again
                    ANSI.printUserWarning("Invalid account type. Acceptable values: CHECKING, SAVINGS");
                    log.warn("User inputted invalid account type: {{}}. Acceptable values: {CHECKING, SAVINGS}", accountTypeInput);
                }
            }

            // Prompt user for a new 4-digit PIN
            System.out.print("Create a 4-digit PIN for your account: ");
            int pin = BankScanner.promptUserForPIN();

            // Prompt user to re-enter 4-digit PIN
            System.out.print("Re-enter your PIN: ");
            int pinConfirmation = BankScanner.promptUserForPIN();

            // If PINs don't match, keep asking again
            while (pin != pinConfirmation) {
                ANSI.printUserWarning("PINs do not match.");
                System.out.print("Re-enter your PIN: ");
                log.warn("PINs entered do not match.");
                pinConfirmation = BankScanner.promptUserForPIN();
            }

            log.info("PIN accepted.");

            // Try creating a new account for user
            long accountNumber;
            try {
                // Attempt new account creation
                accountNumber = accountService.createAccount(user, pin, accountType);

                // Account creation failed
                if (accountNumber == -1) {
                    System.out.println("Account creation failed. Please try again...");
                    log.error("Account creation failed for user: {}", user.getUsername());
                    continue;
                }
            } catch (IllegalArgumentException e) {
                // Something else went wrong, check logs for more info
                System.out.println("Something went wrong. Please try again...");
                log.error("Something went wrong. {}", e.getMessage());
                continue;
            }

            // Account successfully created
            System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                    "Account successfully created!" +
                    ANSI.RESET);

            log.info("Account successfully created! {username: {}, account: {}}", user.getUsername(), accountType + String.format(" ****%04d", accountNumber % 10000));

            // Display new account info
            System.out.println("Please note the account number for your information:");
            System.out.println(ANSI.MAGENTA + accountNumber + ANSI.RESET);

            // Freeze the screen
            System.out.print(ANSI.rgb(100, 255, 100) + "\nPress enter to continue..." + ANSI.RESET);
            BankScanner.freeze();
            break;
        }
    }
}
